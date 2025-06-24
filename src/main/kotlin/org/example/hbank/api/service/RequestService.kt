package org.example.hbank.api.service

import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.mapper.TransactionMapper
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.model.TransactionToken
import org.example.hbank.api.repository.AccountRepository
import org.example.hbank.api.repository.TokenRepository
import org.example.hbank.api.repository.TransactionRepository
import org.example.hbank.api.repository.TransactionTokenRepository
import org.example.hbank.api.request.*
import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.*

interface RequestService {
    fun createRequest(username: String, request: CreateRequestRequest): TransactionResponse
    fun acceptRequest(username: String, request: AcceptRequestRequest): TransactionResponse
    fun declineRequest(username: String, request: DeclineRequestRequest): TransactionResponse
    fun verifyRequest(username: String, request: VerifyRequestRequest)
    fun confirmRequest(username: String, request: ConfirmRequestRequest): TransactionResponse
    fun cancelRequest(username: String, request: CancelRequestRequest): TransactionResponse
}

@Service
class RequestServiceImpl(
    private val clock: Clock,
    private val accountRepository: AccountRepository,
    private val tokenRepository: TokenRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionTokenRepository: TransactionTokenRepository,
    private val tokenMapper: TokenMapper,
    private val transactionMapper: TransactionMapper,
    private val mailSender: MailSender,
) : RequestService {

    @Transactional
    override fun createRequest(username: String, request: CreateRequestRequest): TransactionResponse {
        val to = getToAccount(username = username)
        val from = getFromAccount(identifierType = request.identifierType, identifier = request.identifierValue)

        validateAccounts(from = from, to = to)
        validateRequest(account = to, amount = request.amount)

        val transaction = transactionRepository.save(
            transactionMapper
                .toEntity(
                    reference = generateTransactionReference(),
                    amount = request.amount,
                    type = TransactionType.REQUEST,
                    from = from,
                    to = to,
                )
        )

        mailSender.sendEmail(
            email = EmailContent.ReceivedRequest(
                to = transaction.from.customer.user.email,
                username = transaction.from.customer.user.username,
                account = transaction.to.customer.user.username,
                amount = transaction.amount
            )
        )

        return transactionMapper.toResponse(transaction = transaction)
    }

    @Transactional
    override fun acceptRequest(username: String, request: AcceptRequestRequest): TransactionResponse {
        val transaction = transactionRepository.findTransactionByReference(reference = request.reference)
            ?: throw TransactionNotFoundException()

        if (transaction.from.customer.user.username != username)
            throw TransactionNotFoundException()

        if (transaction.type != TransactionType.REQUEST)
            throw TransactionNotFoundException()

        if (transaction.status != TransactionStatus.CREATED)
            throw TransactionNotFoundException()

        return transactionMapper.toResponse(
            transaction = transactionRepository.save(
                transactionMapper.updateEntity(transaction = transaction.copy(status = TransactionStatus.ACCEPTED))
            )
        )
    }

    @Transactional
    override fun declineRequest(username: String, request: DeclineRequestRequest): TransactionResponse {
        val transaction = transactionRepository.findTransactionByReference(reference = request.reference)
            ?: throw TransactionNotFoundException()

        if (transaction.to.customer.user.username != username)
            throw TransactionNotFoundException()

        if (transaction.type != TransactionType.REQUEST)
            throw TransactionNotFoundException()

        if (transaction.status != TransactionStatus.CREATED)
            throw TransactionNotFoundException()

        val savedTransaction = transactionRepository
            .save(
                transaction.copy(
                    status = TransactionStatus.ACCEPTED,
                    modifiedAt = clock.instant()
                )
            )

        mailSender.sendEmail(
            email = EmailContent.DeclinedRequest(
                to = savedTransaction.to.customer.user.email,
                username = savedTransaction.to.customer.user.username,
                recipient = savedTransaction.from.customer.user.username
            )
        )

        return transactionMapper.toResponse(transaction = savedTransaction)
    }

    @Transactional
    override fun verifyRequest(username: String, request: VerifyRequestRequest) {
        val transaction = transactionRepository.findTransactionByReference(reference = request.reference)
            ?: throw TransactionNotFoundException()

        if (transaction.to.customer.user.username != username)
            throw TransactionNotFoundException()

        if (transaction.type != TransactionType.REQUEST)
            throw TransactionNotFoundException()

        if (transaction.status != TransactionStatus.ACCEPTED)
            throw TransactionNotFoundException()

        val transactionToken = createTransactionToken(transaction = transaction)

        mailSender.sendEmail(
            email = EmailContent.VerifyRequest(
                to = transactionToken.transaction.to.customer.user.email,
                username = transactionToken.transaction.to.customer.user.username,
                token = transactionToken.token.value
            )
        )
    }

    @Transactional
    override fun confirmRequest(username: String, request: ConfirmRequestRequest): TransactionResponse {
        val transactionToken = transactionTokenRepository
            .findTransactionTokensByTokenValue(value = request.token)
            ?: throw VerificationCodeNotFoundException()

        if (!tokenMapper.isExpired(token = transactionToken.token))
            throw VerificationCodeExpiredException()

        if (transactionToken.transaction.type != TransactionType.REQUEST)
            throw TransactionNotFoundException()

        validateAccounts(from = transactionToken.transaction.from, to = transactionToken.transaction.to)

        if (transactionToken.transaction.to.customer.user.username != username)
            throw TransactionNotFoundException()

        if (hasInsufficientFunds(
                account = transactionToken.transaction.to,
                amount = transactionToken.transaction.amount
            )
        )
            throw AccountInsufficientFundsException()

        if (transactionToken.transaction.status != TransactionStatus.ACCEPTED)
            throw TransactionNotFoundException()

        val transaction = transactionRepository.save(
            transactionToken.transaction.copy(
                status = TransactionStatus.COMPLETED,
                modifiedAt = clock.instant()
            )
        )

        accountRepository.save(
            transaction.from.copy(
                balance = transaction.to.balance.minus(transaction.amount),
                modifiedAt = clock.instant()
            )
        )

        accountRepository.save(
            transaction.to.copy(
                balance = transaction.from.balance.plus(transaction.amount),
                modifiedAt = clock.instant()
            )
        )

        transactionTokenRepository.deleteTransactionTokensByTransactionId(id = transaction.id!!)

        mailSender.sendEmail(
            email = EmailContent.AcceptedRequest(
                to = transaction.to.customer.user.email,
                username = transaction.to.customer.user.username,
                recipient = transaction.from.customer.user.username,
            )
        )

        return transactionMapper.toResponse(transaction)
    }

    @Transactional
    override fun cancelRequest(username: String, request: CancelRequestRequest): TransactionResponse {
        val transaction = transactionRepository.findTransactionByReference(reference = request.reference)
            ?: throw TransactionNotFoundException()

        if (transaction.to.customer.user.username != username)
            throw TransactionNotFoundException()

        if (transaction.type != TransactionType.REQUEST)
            throw TransactionNotFoundException()

        if (transaction.status != TransactionStatus.CREATED)
            throw TransactionNotFoundException()

        return transactionMapper.toResponse(
            transaction = transactionRepository.save(
                transaction.copy(
                    status = TransactionStatus.CANCELED,
                    modifiedAt = clock.instant()
                )
            )
        )
    }

    private fun getToAccount(username: String): Account = accountRepository
        .findAccountByCustomerUserUsernameAndType(username = username, AccountType.PERSONAL_ACCOUNT)
        ?: throw AccountNotFoundException()

    private fun getFromAccount(identifierType: RequestIdentifierType, identifier: String): Account =
        when (identifierType) {
            RequestIdentifierType.USERNAME -> {
                accountRepository
                    .findAccountByCustomerUserUsernameAndType(username = identifier, AccountType.PERSONAL_ACCOUNT)
                    ?: throw AccountNotFoundException()
            }

            RequestIdentifierType.EMAIL -> {
                accountRepository
                    .findAccountByCustomerUserEmailAndType(email = identifier, AccountType.PERSONAL_ACCOUNT)
                    ?: throw AccountNotFoundException()
            }

            RequestIdentifierType.PHONE_NUMBER -> {
                accountRepository
                    .findAccountByCustomerUserPhoneNumberAndType(phoneNumber = identifier, AccountType.PERSONAL_ACCOUNT)
                    ?: throw AccountNotFoundException()
            }

            RequestIdentifierType.ACCOUNT_NUMBER -> {
                accountRepository.findAccountByNumber(number = identifier)
                    ?: throw AccountNotFoundException()
            }
        }

    private fun validateAccounts(from: Account, to: Account) {

        if (isSameAccount(from = from, to = to))
            throw SelfTransactionProhibitedException()

        if (isAccountNotActivated(account = from))
            throw AccountInvalidStatusException()

        if (isAccountNotActivated(account = to))
            throw AccountInvalidStatusException()
    }

    private fun validateRequest(account: Account, amount: Double) {

        if (hasAccountReachedRequestAmountLimits(account = account, amount = amount))
            throw AccountRequestAmountLimitException()

        if (hasAccountReachedRequestNumberLimits(account = account))
            throw AccountRequestNumberLimitException()
    }

    private fun hasAccountReachedRequestAmountLimits(account: Account, amount: Double): Boolean {
        val isWithinTransactionAmount =
            amount in account.limit.maximumTransactionAmount..account.limit.maximumTransactionAmount
        val reachedDailyAmountLimit = reachDailyRequestAmountLimit(account = account, amount = amount)
        val reachedMonthlyAmountLimit = reachMonthlyRequestAmountLimit(account = account, amount = amount)
        val reachedYearlyAmountLimit = reachYearlyRequestAmountLimit(account = account, amount = amount)
        return reachedDailyAmountLimit || reachedMonthlyAmountLimit || reachedYearlyAmountLimit || isWithinTransactionAmount
    }

    private fun hasAccountReachedRequestNumberLimits(account: Account): Boolean {
        val reachDailyNumberLimit = reachDailyRequestNumberLimit(account = account)
        val reachMonthlyNumberLimit = reachMonthlyRequestNumberLimit(account = account)
        val reachYearlyNumberLimit = reachYearlyRequestNumberLimit(account = account)
        return reachDailyNumberLimit || reachMonthlyNumberLimit || reachYearlyNumberLimit
    }

    private fun reachDailyRequestAmountLimit(account: Account, amount: Double): Boolean {
        val sum = transactionRepository.sumTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = account.id!!,
            type = TransactionType.REQUEST,
            status = listOf(
                TransactionStatus.CREATED,
                TransactionStatus.ACCEPTED,
                TransactionStatus.DECLINED,
                TransactionStatus.COMPLETED
            ),
            start = clock.instant().startOfTheDay(),
            end = clock.instant().endOfTheDay()
        )

        return sum.plus(other = amount) >= account.limit.dailyRequestsAmountLimit
    }

    private fun reachMonthlyRequestAmountLimit(account: Account, amount: Double): Boolean {
        val sum = transactionRepository.sumTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = account.id!!,
            type = TransactionType.REQUEST,
            status = listOf(
                TransactionStatus.CREATED,
                TransactionStatus.ACCEPTED,
                TransactionStatus.DECLINED,
                TransactionStatus.COMPLETED
            ),
            start = clock.instant().startOfTheMonth(),
            end = clock.instant().endOfTheMonth()
        )

        return sum.plus(other = amount) >= account.limit.monthlyRequestsAmountLimit
    }

    private fun reachYearlyRequestAmountLimit(account: Account, amount: Double): Boolean {
        val sum = transactionRepository.sumTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = account.id!!,
            type = TransactionType.REQUEST,
            status = listOf(
                TransactionStatus.CREATED,
                TransactionStatus.ACCEPTED,
                TransactionStatus.DECLINED,
                TransactionStatus.COMPLETED
            ),
            start = clock.instant().startOfTheYear(),
            end = clock.instant().endOfTheYear()
        )

        return sum.plus(other = amount) >= account.limit.yearlyRequestsAmountLimit
    }

    private fun reachDailyRequestNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = account.id!!,
            type = TransactionType.REQUEST,
            status = listOf(
                TransactionStatus.CREATED,
                TransactionStatus.ACCEPTED,
                TransactionStatus.DECLINED,
                TransactionStatus.COMPLETED
            ),
            start = clock.instant().startOfTheDay(),
            end = clock.instant().endOfTheDay()
        ) >= account.limit.maximumDailyRequestNumber

    private fun reachMonthlyRequestNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = account.id!!,
            type = TransactionType.REQUEST,
            status = listOf(
                TransactionStatus.CREATED,
                TransactionStatus.ACCEPTED,
                TransactionStatus.DECLINED,
                TransactionStatus.COMPLETED
            ),
            start = clock.instant().startOfTheMonth(),
            end = clock.instant().endOfTheMonth()
        ) >= account.limit.maximumMonthlyRequestNumber

    private fun reachYearlyRequestNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = account.id!!,
            type = TransactionType.REQUEST,
            status = listOf(
                TransactionStatus.CREATED,
                TransactionStatus.ACCEPTED,
                TransactionStatus.DECLINED,
                TransactionStatus.COMPLETED
            ),
            start = clock.instant().startOfTheYear(),
            end = clock.instant().endOfTheYear()
        ) >= account.limit.maximumYearlyRequestNumber

    private fun generateTransactionReference(): UUID {
        var reference: UUID
        do {
            reference = UUID.randomUUID()
        } while (transactionRepository.existsTransactionByReference(reference = reference))

        return reference
    }

    private fun isSameAccount(from: Account, to: Account): Boolean = from.id == to.id

    private fun isAccountNotActivated(account: Account): Boolean = account.status != AccountStatus.ACTIVATED

    private fun hasInsufficientFunds(account: Account, amount: Double): Boolean =
        account.balance < amount

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
}
