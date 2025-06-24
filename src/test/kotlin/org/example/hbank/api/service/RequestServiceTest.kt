package org.example.hbank.api.service

import org.example.hbank.api.mapper.AccountMapper
import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.mapper.TransactionMapper
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.repository.AccountRepository
import org.example.hbank.api.repository.TokenRepository
import org.example.hbank.api.repository.TransactionRepository
import org.example.hbank.api.repository.TransactionTokenRepository
import org.example.hbank.api.request.CreateRequestRequest
import org.example.hbank.api.util.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Clock
import java.time.Instant
import java.util.*

class RequestServiceTest {

    private val clock = mock<Clock>()
    private val accountRepository = mock<AccountRepository>()
    private val accountMapper = mock<AccountMapper>()
    private val tokenRepository = mock<TokenRepository>()
    private val transactionRepository = mock<TransactionRepository>()
    private val transactionTokenRepository = mock<TransactionTokenRepository>()
    private val tokenMapper = mock<TokenMapper>()
    private val transactionMapper = mock<TransactionMapper>()
    private val mailSender = mock<MailSender>()

    private val requestService: RequestService = RequestServiceImpl(
        clock = clock,
        accountRepository = accountRepository,
        tokenRepository = tokenRepository,
        transactionRepository = transactionRepository,
        transactionTokenRepository = transactionTokenRepository,
        tokenMapper = tokenMapper,
        transactionMapper = transactionMapper,
        mailSender = mailSender
    )

    @Test
    fun `should create request by username successfully`() {
        // given
        val username = "test_username1"
        val request = CreateRequestRequest(
            identifierValue = "test_username2",
            identifierType = RequestIdentifierType.USERNAME,
            amount = 1500.0,
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), username = request.identifierValue)

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transaction = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = request.amount,
            type = TransactionType.REQUEST,
            status = TransactionStatus.CREATED,
            to = account1,
            from = account2
        )

        given(methodCall = clock.instant()).willReturn(Instant.now())
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account1)
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = request.identifierValue,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account2)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.REQUEST,
                status = listOf(TransactionStatus.COMPLETED),
                start = clock.instant().startOfTheDay(),
                end = clock.instant().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.REQUEST,
                status = listOf(TransactionStatus.COMPLETED),
                start = clock.instant().startOfTheMonth(),
                end = clock.instant().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.REQUEST,
                status = listOf(TransactionStatus.COMPLETED),
                start = clock.instant().startOfTheYear(),
                end = clock.instant().endOfTheYear()
            )
        ).willReturn(400000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.REQUEST,
                status = listOf(TransactionStatus.COMPLETED),
                start = clock.instant().startOfTheDay(),
                end = clock.instant().startOfTheDay()
            )
        ).willReturn(0)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.REQUEST,
                status = listOf(TransactionStatus.COMPLETED),
                start = clock.instant().startOfTheMonth(),
                end = clock.instant().endOfTheMonth()
            )
        ).willReturn(2)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.REQUEST,
                status = listOf(TransactionStatus.COMPLETED),
                start = clock.instant().startOfTheYear(),
                end = clock.instant().endOfTheYear()
            )
        ).willReturn(8)
        given(
            methodCall = transactionRepository.existsTransactionByReference(reference = reference)
        ).willReturn(false)
        given(
            methodCall = transactionMapper.toEntity(
                reference = reference,
                amount = request.amount,
                type = TransactionType.REQUEST,
                from = account2,
                to = account1
            )
        ).willReturn(transaction)
        given(
            methodCall = transactionRepository.save(any<Transaction>())
        ).willReturn(transaction)
        doNothing()
            .`when`(mailSender)
            .sendEmail(
                email = EmailContent.ReceivedRequest(
                    to = transaction.from.customer.user.email,
                    username = transaction.from.customer.user.username,
                    account = transaction.to.customer.user.username,
                    amount = transaction.amount
                )
            )
        given(
            methodCall = transactionMapper.toResponse(transaction = transaction)
        ).willReturn(TestResponseFactory.toTransactionResponse(transaction = transaction))

        // when
        val result = requestService.createRequest(username = username, request = request)

        // then
        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = any<UUID>(),
                type = eq(value = TransactionType.REQUEST),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.REQUEST),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository).existsTransactionByReference(reference = any<UUID>())
        verify(mock = transactionRepository).save(any<Transaction>())

        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.REQUEST)
        assert(value = result.status == TransactionStatus.CREATED)
    }

    @Test
    fun `should create request by email successfully`() {
    }

    @Test
    fun `should create request by phone number successfully`() {
    }

    @Test
    fun `should create request by account number successfully`() {
    }
}