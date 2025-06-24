package org.example.hbank.api.service

import org.example.hbank.api.mapper.AccountMapper
import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.mapper.TransactionMapper
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.model.TransactionToken
import org.example.hbank.api.repository.*
import org.example.hbank.api.request.ConfirmTransferRequest
import org.example.hbank.api.request.CreateTransferRequest
import org.example.hbank.api.request.VerifyTransferRequest
import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.RoundingMode
import java.time.Clock
import java.util.*

interface TransferService {
    fun createTransfer(username: String, request: CreateTransferRequest): TransactionResponse
    fun verifyTransfer(username: String, request: VerifyTransferRequest)
    fun confirmTransfer(username: String, request: ConfirmTransferRequest): TransactionResponse
}

@Service
class TransferServiceImpl(
    private val clock: Clock,
    private val accountRepository: AccountRepository,
    private val accountTokenRepository: AccountTokenRepository,
    private val tokenRepository: TokenRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionTokenRepository: TransactionTokenRepository,
    private val accountMapper: AccountMapper,
    private val tokenMapper: TokenMapper,
    private val transactionMapper: TransactionMapper,
    private val mailSender: MailSender
) : TransferService {

    @Transactional
    override fun createTransfer(username: String, request: CreateTransferRequest): TransactionResponse {
        val from = getFromAccount(username = username)
        val to = getToAccount(identifierType = request.identifierType, identifier = request.identifierValue)

        validateAccounts(from = from, to = to)
        validateTransfer(account = from, amount = request.amount)

        if (hasInsufficientFunds(account = from, amount = request.amount))
            throw AccountInsufficientFundsException()

        val transaction = transactionRepository.save(
            transactionMapper
                .toEntity(
                    reference = generateTransactionReference(),
                    amount = request.amount,
                    type = TransactionType.TRANSFER,
                    from = from,
                    to = to
                )
        )

        val transactionToken = createTransactionToken(transaction = transaction)

        mailSender.sendEmail(
            email = EmailContent.VerifyTransfer(
                to = transactionToken.transaction.from.customer.user.email,
                username = transactionToken.transaction.from.customer.user.username,
                token = transactionToken.token.value
            )
        )

        return transactionMapper.toResponse(transaction = transactionToken.transaction)
    }

    @Transactional
    override fun verifyTransfer(username: String, request: VerifyTransferRequest) {
        val transaction = transactionRepository.findTransactionByReference(reference = request.reference)
            ?: throw TransactionNotFoundException()

        if (transaction.from.customer.user.username != username)
            throw TransactionNotFoundException()

        if (transaction.type != TransactionType.TRANSFER)
            throw TransactionNotFoundException()

        if (transaction.status != TransactionStatus.CREATED)
            throw TransactionNotFoundException()

        val transactionToken = createTransactionToken(transaction = transaction)

        mailSender.sendEmail(
            email = EmailContent.VerifyTransfer(
                to = transactionToken.transaction.from.customer.user.email,
                username = transactionToken.transaction.from.customer.user.username,
                token = transactionToken.token.value
            )
        )
    }

    @Transactional
    override fun confirmTransfer(username: String, request: ConfirmTransferRequest): TransactionResponse {
        val transactionToken = transactionTokenRepository
            .findTransactionTokensByTokenValue(value = request.token)
            ?: throw VerificationCodeNotFoundException()

        if (transactionToken.transaction.from.customer.user.username != username)
            throw TransactionNotFoundException()

        if (tokenMapper.isExpired(token = transactionToken.token))
            throw VerificationCodeExpiredException()

        if (transactionToken.transaction.type != TransactionType.TRANSFER)
            throw TransactionNotFoundException()

        if (transactionToken.transaction.status != TransactionStatus.CREATED)
            throw TransactionNotFoundException()

        if (hasInsufficientFunds(
                account = transactionToken.transaction.from,
                amount = transactionToken.transaction.amount
            )
        )
            throw AccountInsufficientFundsException()

        val transaction = transactionRepository.save(
            transactionMapper.updateEntity(
                transaction = transactionToken.transaction.copy(
                    status = TransactionStatus.COMPLETED,
                    fees = calculateTransferFees(amount = transactionToken.transaction.amount)
                )
            )
        )

        accountRepository.save(
            accountMapper.updateEntity(
                account = transaction.from.copy(
                    balance = transaction.from.balance.minus(transaction.amount + transaction.fees)
                )
            )
        )

        accountRepository.save(
            accountMapper.updateEntity(
                account = transaction.to.copy(
                    balance = transaction.to.balance.plus(transaction.amount)
                )
            )
        )

        transactionTokenRepository.deleteTransactionTokensByTransactionId(id = transaction.id!!)

        mailSender.sendEmail(
            email = EmailContent.SuccessTransfer(
                to = transaction.from.customer.user.email,
                username = transaction.from.customer.user.username,
                amount = transaction.amount,
                recipient = transaction.to.customer.user.username,
                account = transaction.to.number,
                time = transaction.modifiedAt
            )
        )

        mailSender.sendEmail(
            email = EmailContent.ReceivedTransfer(
                to = transaction.to.customer.user.email,
                username = transaction.to.customer.user.username,
                amount = transaction.amount,
                account = transaction.from.customer.user.username,
                time = transaction.modifiedAt,
            )
        )

        return transactionMapper.toResponse(transaction)
    }

    private fun validateAccounts(from: Account, to: Account) {

        if (isSameAccount(from = from, to = to))
            throw SelfTransactionProhibitedException()

        if (isAccountNotActivated(account = from))
            throw AccountInvalidStatusException()

        if (isAccountNotActivated(account = to))
            throw AccountInvalidStatusException()
    }

    private fun validateTransfer(account: Account, amount: Double) {

        if (hasAccountReachedTransferAmountLimits(account = account, amount = amount))
            throw AccountTransferAmountLimitException()

        if (hasAccountReachedTransferNumberLimits(account = account))
            throw AccountTransferNumberLimitException()
    }

    private fun generateTransactionReference(): UUID {
        var reference: UUID
        do {
            reference = UUID.randomUUID()
        } while (transactionRepository.existsTransactionByReference(reference = reference))

        return reference
    }

    private fun hasAccountReachedTransferAmountLimits(account: Account, amount: Double): Boolean {
        val isWithinTransactionAmount =
            amount in account.limit.maximumTransactionAmount..account.limit.maximumTransactionAmount
        val reachedDailyAmountLimit = reachDailyTransferAmountLimit(account = account, amount = amount)
        val reachedMonthlyAmountLimit = reachMonthlyTransferAmountLimit(account = account, amount = amount)
        val reachedYearlyAmountLimit = reachYearlyTransferAmountLimit(account = account, amount = amount)
        return reachedDailyAmountLimit || reachedMonthlyAmountLimit || reachedYearlyAmountLimit || isWithinTransactionAmount
    }

    private fun hasAccountReachedTransferNumberLimits(account: Account): Boolean {
        val reachDailyNumberLimit = reachDailyTransferNumberLimit(account = account)
        val reachMonthlyNumberLimit = reachMonthlyTransferNumberLimit(account = account)
        val reachYearlyNumberLimit = reachYearlyTransferNumberLimit(account = account)
        return reachDailyNumberLimit || reachMonthlyNumberLimit || reachYearlyNumberLimit
    }

    private fun reachDailyTransferAmountLimit(account: Account, amount: Double): Boolean {
        val sum = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = account.id!!,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = clock.instant().startOfTheDay(),
            end = clock.instant().endOfTheDay()
        )

        return sum.plus(other = amount) >= account.limit.dailyTransfersAmountLimit
    }

    private fun reachMonthlyTransferAmountLimit(account: Account, amount: Double): Boolean {
        val sum = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = account.id!!,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = clock.instant().startOfTheMonth(),
            end = clock.instant().endOfTheMonth()
        )

        return sum.plus(other = amount) >= account.limit.monthlyTransfersAmountLimit
    }

    private fun reachYearlyTransferAmountLimit(account: Account, amount: Double): Boolean {
        val sum = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = account.id!!,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = clock.instant().startOfTheYear(),
            end = clock.instant().endOfTheYear()
        )

        return sum.plus(other = amount) >= account.limit.yearlyTransfersAmountLimit
    }

    private fun reachDailyTransferNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = account.id!!,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = clock.instant().startOfTheDay(),
            end = clock.instant().endOfTheDay()
        ) >= account.limit.maximumDailyTransferNumber

    private fun reachMonthlyTransferNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = account.id!!,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = clock.instant().startOfTheMonth(),
            end = clock.instant().endOfTheMonth()
        ) >= account.limit.maximumMonthlyTransferNumber

    private fun reachYearlyTransferNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = account.id!!,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = clock.instant().startOfTheYear(),
            end = clock.instant().endOfTheYear()
        ) >= account.limit.maximumYearlyTransferNumber

    private fun getFromAccount(username: String): Account = accountRepository
        .findAccountByCustomerUserUsernameAndType(username = username, type = AccountType.PERSONAL_ACCOUNT)
        ?: throw AccountNotFoundException()

    private fun getToAccount(identifierType: TransferIdentifierType, identifier: String): Account =
        when (identifierType) {
            TransferIdentifierType.USERNAME -> {
                accountRepository
                    .findAccountByCustomerUserUsernameAndType(username = identifier, AccountType.PERSONAL_ACCOUNT)
                    ?: throw AccountNotFoundException()
            }

            TransferIdentifierType.EMAIL -> {
                accountRepository
                    .findAccountByCustomerUserEmailAndType(email = identifier, AccountType.PERSONAL_ACCOUNT)
                    ?: throw AccountNotFoundException()
            }

            TransferIdentifierType.PHONE_NUMBER -> {
                accountRepository
                    .findAccountByCustomerUserPhoneNumberAndType(phoneNumber = identifier, AccountType.PERSONAL_ACCOUNT)
                    ?: throw AccountNotFoundException()
            }

            TransferIdentifierType.ACCOUNT_NUMBER -> {
                accountRepository.findAccountByNumber(number = identifier)
                    ?: throw AccountNotFoundException()
            }

            TransferIdentifierType.ACCOUNT_TOKEN -> {
                accountTokenRepository.findAccountTokenByTokenValue(value = identifier)?.account
                    ?: throw AccountNotFoundException()
            }
        }

    private fun isSameAccount(from: Account, to: Account): Boolean = from.id == to.id

    private fun isAccountNotActivated(account: Account): Boolean = account.status != AccountStatus.ACTIVATED

    private fun hasInsufficientFunds(account: Account, amount: Double): Boolean =
        account.balance < amount

    private fun calculateTransferFees(amount: Double): Double {

        val fees = when (amount) {
            in 0.0..<10000.0 -> BASE_FEE
            in 10000.0..<20000.0 -> amount.times(FEE_RATE_TIER_1).plus(BASE_FEE)
            in 20000.0..<50000.0 -> amount.times(FEE_RATE_TIER_2).plus(BASE_FEE)
            in 50000.0..<80000.0 -> amount.times(FEE_RATE_TIER_3).plus(BASE_FEE)
            in 80000.0..<100000.0 -> amount.times(FEE_RATE_TIER_4).plus(BASE_FEE)
            else -> amount.times(FEE_RATE_TIER_5).plus(BASE_FEE)
        }
        return fees.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    private fun createTransactionToken(transaction: Transaction): TransactionToken {
        transactionTokenRepository.deleteTransactionTokensByTransactionId(id = transaction.id!!)
        val token = tokenRepository.save(tokenMapper.toEntity(value = generateTokenValue()))

        return transactionTokenRepository.save(TransactionToken(transaction = transaction, token = token))
    }

    private fun generateTokenValue(): String {
        var value: String
        do {
            value = Generator.generateDecString(length = 6)
        } while (tokenRepository.existsTokenByValue(value = value))

        return value
    }

    companion object {
        private const val BASE_FEE = 10.0
        private const val FEE_RATE_TIER_1 = 0.0005
        private const val FEE_RATE_TIER_2 = 0.0008
        private const val FEE_RATE_TIER_3 = 0.001
        private const val FEE_RATE_TIER_4 = 0.0015
        private const val FEE_RATE_TIER_5 = 0.0025
    }
}

