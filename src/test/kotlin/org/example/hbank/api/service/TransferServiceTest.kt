package org.example.hbank.api.service

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.example.hbank.api.mapper.AccountMapper
import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.mapper.TransactionMapper
import org.example.hbank.api.model.Token
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.model.TransactionToken
import org.example.hbank.api.repository.*
import org.example.hbank.api.request.ConfirmTransferRequest
import org.example.hbank.api.request.CreateTransferRequest
import org.example.hbank.api.request.VerifyTransferRequest
import org.example.hbank.api.util.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Clock
import java.time.Instant
import java.util.*

class TransferServiceTest {

    private val clock = mock<Clock>()
    private val accountRepository = mock<AccountRepository>()
    private val accountTokenRepository = mock<AccountTokenRepository>()
    private val accountMapper = mock<AccountMapper>()
    private val tokenRepository = mock<TokenRepository>()
    private val transactionRepository = mock<TransactionRepository>()
    private val transactionTokenRepository = mock<TransactionTokenRepository>()
    private val tokenMapper = mock<TokenMapper>()
    private val transactionMapper = mock<TransactionMapper>()
    private val mailSender = mock<MailSender>()

    private val transferService: TransferService = TransferServiceImpl(
        clock = clock,
        accountRepository = accountRepository,
        accountTokenRepository = accountTokenRepository,
        tokenRepository = tokenRepository,
        transactionRepository = transactionRepository,
        transactionTokenRepository = transactionTokenRepository,
        accountMapper = accountMapper,
        tokenMapper = tokenMapper,
        transactionMapper = transactionMapper,
        mailSender = mailSender
    )

    @BeforeEach
    fun setup() {
        mockkObject(Generator)
    }

    @AfterEach
    fun cleanup() {
        unmockkObject(Generator)
    }

    @Test
    fun `should create transfer by username successfully`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
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
            balance = 25000.0,
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
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transaction,
            token = token,
        )

        every { Generator.generateDecString() } returns tokenValue

        whenever(methodCall = clock.instant()).thenReturn(Instant.now())
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
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(2200000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().startOfTheDay()
            )
        ).willReturn(1)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(20)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(222)
        given(
            transactionRepository.existsTransactionByReference(reference = any())
        ).willReturn(false)
        given(
            methodCall = transactionMapper.toEntity(
                reference = reference,
                amount = request.amount,
                type = TransactionType.TRANSFER,
                from = account1,
                to = account2
            )
        ).willReturn(transaction)
        given(
            methodCall = transactionRepository.save(transaction)
        ).willReturn(transaction)
        doNothing()
            .`when`(transactionTokenRepository)
            .deleteTransactionTokensByTransactionId(id = transaction.id!!)
        given(
            methodCall = tokenRepository.existsTokenByValue(token.value)
        ).willReturn(false)
        given(
            methodCall = tokenMapper.toEntity(value = tokenValue)
        ).willReturn(token)
        given(
            methodCall = tokenRepository.save(token)
        ).willReturn(token)
        given(
            methodCall = transactionTokenRepository.save(transactionToken)
        ).willReturn(transactionToken)
        doNothing()
            .`when`(mailSender)
            .sendEmail(
                email = EmailContent.VerifyTransfer(
                    to = transactionToken.transaction.from.customer.user.email,
                    username = transactionToken.transaction.from.customer.user.username,
                    token = transactionToken.token.value
                )
            )
        given(
            methodCall = transactionMapper.toResponse(transaction = transaction)
        ).willReturn(TestResponseFactory.toTransactionResponse(transaction = transaction))

        // when
        val result = transferService.createTransfer(username = username, request = request)

        // then
        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = any<UUID>(),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository).existsTransactionByReference(reference = any<UUID>())
        verify(mock = transactionRepository).save(any<Transaction>())
        verify(mock = transactionTokenRepository).deleteTransactionTokensByTransactionId(id = transaction.id!!)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = transactionTokenRepository).save(any<TransactionToken>())

        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.TRANSFER)
        assert(value = result.status == TransactionStatus.CREATED)
    }

    @Test
    fun `should create transfer by email successfully`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test@example.com",
            identifierType = TransferIdentifierType.EMAIL,
            amount = 1500.0,
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), email = request.identifierValue)

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = request.amount,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token
        )

        every { Generator.generateDecString() } returns tokenValue

        given(methodCall = clock.instant()).willReturn(Instant.now())
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account1)
        given(
            methodCall = accountRepository.findAccountByCustomerUserEmailAndType(
                email = request.identifierValue,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account2)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(2200000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(1)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(20)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(222)
        given(
            transactionRepository.existsTransactionByReference(reference = any())
        ).willReturn(false)
        given(
            methodCall = transactionMapper.toEntity(
                reference = reference,
                amount = request.amount,
                type = TransactionType.TRANSFER,
                from = account1,
                to = account2
            )
        ).willReturn(transfer)
        given(
            methodCall = transactionRepository.save(transfer)
        ).willReturn(transfer)
        doNothing()
            .`when`(transactionTokenRepository)
            .deleteTransactionTokensByTransactionId(id = transfer.id!!)
        given(
            methodCall = tokenRepository.existsTokenByValue(token.value)
        ).willReturn(false)
        given(
            methodCall = tokenMapper.toEntity(value = tokenValue)
        ).willReturn(token)
        given(
            methodCall = tokenRepository.save(token)
        ).willReturn(token)
        given(
            methodCall = transactionTokenRepository.save(transactionToken)
        ).willReturn(transactionToken)
        doNothing()
            .`when`(mailSender)
            .sendEmail(
                email = EmailContent.VerifyTransfer(
                    to = transactionToken.transaction.from.customer.user.email,
                    username = transactionToken.transaction.from.customer.user.username,
                    token = transactionToken.token.value
                )
            )
        given(
            methodCall = transactionMapper.toResponse(transaction = transfer)
        ).willReturn(TestResponseFactory.toTransactionResponse(transaction = transfer))

        // when
        val result = transferService.createTransfer(username = username, request = request)

        // then
        verify(mock = accountRepository)
            .findAccountByCustomerUserUsernameAndType(
                username = eq(value = username),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = accountRepository)
            .findAccountByCustomerUserEmailAndType(
                email = eq(value = request.identifierValue),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = any<UUID>(),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository).existsTransactionByReference(reference = any<UUID>())
        verify(mock = transactionRepository).save(any<Transaction>())
        verify(mock = transactionTokenRepository).deleteTransactionTokensByTransactionId(id = transfer.id!!)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = transactionTokenRepository).save(any<TransactionToken>())

        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.TRANSFER)
        assert(value = result.status == TransactionStatus.CREATED)
    }

    @Test
    fun `should create transfer by phone number successfully`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "+1234567890",
            identifierType = TransferIdentifierType.PHONE_NUMBER,
            amount = 1500.0,
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), phoneNumber = request.identifierValue)

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = request.amount,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token
        )

        every { Generator.generateDecString() } returns tokenValue

        given(methodCall = clock.instant()).willReturn(Instant.now())
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account1)
        given(
            methodCall = accountRepository.findAccountByCustomerUserPhoneNumberAndType(
                phoneNumber = request.identifierValue,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account2)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(2200000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(1)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(20)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(222)
        given(
            transactionRepository.existsTransactionByReference(reference = any())
        ).willReturn(false)
        given(
            methodCall = transactionMapper.toEntity(
                reference = reference,
                amount = request.amount,
                type = TransactionType.TRANSFER,
                from = account1,
                to = account2
            )
        ).willReturn(transfer)
        given(
            methodCall = transactionRepository.save(transfer)
        ).willReturn(transfer)
        doNothing()
            .`when`(transactionTokenRepository)
            .deleteTransactionTokensByTransactionId(id = transfer.id!!)
        given(
            methodCall = tokenRepository.existsTokenByValue(token.value)
        ).willReturn(false)
        given(
            methodCall = tokenMapper.toEntity(value = tokenValue)
        ).willReturn(token)
        given(
            methodCall = tokenRepository.save(token)
        ).willReturn(token)
        given(
            methodCall = transactionTokenRepository.save(transactionToken)
        ).willReturn(transactionToken)
        doNothing()
            .`when`(mailSender)
            .sendEmail(
                email = EmailContent.VerifyTransfer(
                    to = transactionToken.transaction.from.customer.user.email,
                    username = transactionToken.transaction.from.customer.user.username,
                    token = transactionToken.token.value
                )
            )
        given(
            methodCall = transactionMapper.toResponse(transaction = transfer)
        ).willReturn(TestResponseFactory.toTransactionResponse(transaction = transfer))

        // when
        val result = transferService.createTransfer(username = username, request = request)

        // then
        verify(mock = accountRepository)
            .findAccountByCustomerUserUsernameAndType(
                username = eq(value = username),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = accountRepository)
            .findAccountByCustomerUserPhoneNumberAndType(
                phoneNumber = eq(value = request.identifierValue),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = any<UUID>(),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository).existsTransactionByReference(reference = any<UUID>())
        verify(mock = transactionRepository).save(any<Transaction>())
        verify(mock = transactionTokenRepository).deleteTransactionTokensByTransactionId(id = transfer.id!!)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = transactionTokenRepository).save(any<TransactionToken>())

        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.TRANSFER)
        assert(value = result.status == TransactionStatus.CREATED)
    }

    @Test
    fun `should create transfer by account number successfully`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "0987654321",
            identifierType = TransferIdentifierType.ACCOUNT_NUMBER,
            amount = 1500.0,
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), phoneNumber = request.identifierValue)

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = request.amount,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token
        )

        every { Generator.generateDecString() } returns tokenValue

        given(methodCall = clock.instant()).willReturn(Instant.now())
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account1)
        given(
            methodCall = accountRepository.findAccountByNumber(number = request.identifierValue)
        ).willReturn(account2)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(2200000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(1)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(20)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(222)
        given(
            transactionRepository.existsTransactionByReference(reference = any())
        ).willReturn(false)
        given(
            methodCall = transactionMapper.toEntity(
                reference = reference,
                amount = request.amount,
                type = TransactionType.TRANSFER,
                from = account1,
                to = account2
            )
        ).willReturn(transfer)
        given(
            methodCall = transactionRepository.save(transfer)
        ).willReturn(transfer)
        doNothing()
            .`when`(transactionTokenRepository)
            .deleteTransactionTokensByTransactionId(id = transfer.id!!)
        given(
            methodCall = tokenRepository.existsTokenByValue(token.value)
        ).willReturn(false)
        given(
            methodCall = tokenMapper.toEntity(value = tokenValue)
        ).willReturn(token)
        given(
            methodCall = tokenRepository.save(token)
        ).willReturn(token)
        given(
            methodCall = transactionTokenRepository.save(transactionToken)
        ).willReturn(transactionToken)
        doNothing()
            .`when`(mailSender)
            .sendEmail(
                email = EmailContent.VerifyTransfer(
                    to = transactionToken.transaction.from.customer.user.email,
                    username = transactionToken.transaction.from.customer.user.username,
                    token = transactionToken.token.value
                )
            )
        given(
            methodCall = transactionMapper.toResponse(transaction = transfer)
        ).willReturn(TestResponseFactory.toTransactionResponse(transaction = transfer))

        // when
        val result = transferService.createTransfer(username = username, request = request)

        // then
        verify(mock = accountRepository)
            .findAccountByCustomerUserUsernameAndType(
                username = eq(value = username),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = accountRepository)
            .findAccountByNumber(number = eq(value = request.identifierValue))
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = any<UUID>(),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository).existsTransactionByReference(reference = any<UUID>())
        verify(mock = transactionRepository).save(any<Transaction>())
        verify(mock = transactionTokenRepository).deleteTransactionTokensByTransactionId(id = transfer.id!!)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = transactionTokenRepository).save(any<TransactionToken>())

        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.TRANSFER)
        assert(value = result.status == TransactionStatus.CREATED)
    }

    @Test
    fun `should create transfer by account token successfully`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "747568",
            identifierType = TransferIdentifierType.ACCOUNT_TOKEN,
            amount = 1500.0,
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), phoneNumber = request.identifierValue)

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = request.amount,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val tokenValue = "123567"
        val token1 = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)
        val token2 = TestDataFactory.createToken(id = UUID.randomUUID(), value = request.identifierValue)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token1
        )
        val accountToken = TestDataFactory.createAccountToken(
            account = account2,
            token = token2
        )

        every { Generator.generateDecString() } returns tokenValue

        given(methodCall = clock.instant()).willReturn(Instant.now())
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account1)
        given(
            methodCall = accountTokenRepository.findAccountTokenByTokenValue(value = request.identifierValue)
        ).willReturn(accountToken)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(2200000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(1)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(20)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(222)
        given(
            transactionRepository.existsTransactionByReference(reference = any())
        ).willReturn(false)
        given(
            methodCall = transactionMapper.toEntity(
                reference = reference,
                amount = request.amount,
                type = TransactionType.TRANSFER,
                from = account1,
                to = account2
            )
        ).willReturn(transfer)
        given(
            methodCall = transactionRepository.save(transfer)
        ).willReturn(transfer)
        doNothing()
            .`when`(transactionTokenRepository)
            .deleteTransactionTokensByTransactionId(id = transfer.id!!)
        given(
            methodCall = tokenRepository.existsTokenByValue(token1.value)
        ).willReturn(false)
        given(
            methodCall = tokenMapper.toEntity(value = tokenValue)
        ).willReturn(token1)
        given(
            methodCall = tokenRepository.save(token1)
        ).willReturn(token1)
        given(
            methodCall = transactionTokenRepository.save(transactionToken)
        ).willReturn(transactionToken)
        doNothing()
            .`when`(mailSender)
            .sendEmail(
                email = EmailContent.VerifyTransfer(
                    to = transactionToken.transaction.from.customer.user.email,
                    username = transactionToken.transaction.from.customer.user.username,
                    token = transactionToken.token.value
                )
            )
        given(
            methodCall = transactionMapper.toResponse(transaction = transfer)
        ).willReturn(TestResponseFactory.toTransactionResponse(transaction = transfer))

        // when
        val result = transferService.createTransfer(username = username, request = request)

        // then
        verify(mock = accountRepository)
            .findAccountByCustomerUserUsernameAndType(
                username = eq(value = username),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = accountTokenRepository)
            .findAccountTokenByTokenValue(value = eq(value = request.identifierValue))
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = any<UUID>(),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository).existsTransactionByReference(reference = any<UUID>())
        verify(mock = transactionRepository).save(any<Transaction>())
        verify(mock = transactionTokenRepository).deleteTransactionTokensByTransactionId(id = transfer.id!!)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = transactionTokenRepository).save(any<TransactionToken>())

        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.TRANSFER)
        assert(value = result.status == TransactionStatus.CREATED)
    }

    @Test
    fun `should throw exception when creating transfer for non-existent from account`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
            amount = 1500.0,
        )

        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(null)

        // when/then
        assertThrows<AccountNotFoundException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository)
            .findAccountByCustomerUserUsernameAndType(username = username, type = AccountType.PERSONAL_ACCOUNT)
    }

    @Test
    fun `should throw exception when creating transfer for non-existent to account`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
            amount = 1500.0,
        )

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)

        val customer = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user)

        val account = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            balance = 25000.0,
            customer = customer
        )

        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account)
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = request.identifierValue,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(null)

        // when/then
        assertThrows<AccountNotFoundException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
    }

    @Test
    fun `should throw exception when creating transfer for same account`() {
        // given
        val username = "test_username"
        val request = CreateTransferRequest(
            identifierValue = "test_username",
            identifierType = TransferIdentifierType.USERNAME,
            amount = 1500.0,
        )

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)

        val customer = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user)

        val account = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            balance = 25000.0,
            customer = customer
        )

        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = username,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account)
        given(
            methodCall = accountRepository.findAccountByCustomerUserUsernameAndType(
                username = request.identifierValue,
                type = AccountType.PERSONAL_ACCOUNT
            )
        ).willReturn(account)

        // when/then
        assertThrows<SelfTransactionProhibitedException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
    }

    @Test
    fun `should throw exception when creating transfer for from account invalid status`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
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
            balance = 25000.0,
            status = AccountStatus.DEACTIVATED,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

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

        // when/then
        assertThrows<AccountInvalidStatusException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
    }

    @Test
    fun `should throw exception when creating transfer for to account invalid status`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
            amount = 1500.0
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
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            status = AccountStatus.DEACTIVATED,
            customer = customer2
        )

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

        // when/then
        assertThrows<AccountInvalidStatusException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
    }

    @Test
    fun `should throw exception when creating transfer for account amount limits`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
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
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
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
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(3000000.00)

        // when/then
        assertThrows<AccountTransferAmountLimitException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
    }

    @Test
    fun `should throw exception when creating transfer for account number limits`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
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
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
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
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(2200000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(1)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(20)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(360)

        // when/then
        assertThrows<AccountTransferNumberLimitException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
    }

    @Test
    fun `should throw exception when creating transfer for account insufficient funds`() {
        // given
        val username = "test_username1"
        val request = CreateTransferRequest(
            identifierValue = "test_username2",
            identifierType = TransferIdentifierType.USERNAME,
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
            balance = 500.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
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
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(45000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(180000.00)
        given(
            methodCall = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(2200000.00)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheMonth(),
                end = Instant.now().endOfTheMonth()
            )
        ).willReturn(1)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheDay(),
                end = Instant.now().endOfTheDay()
            )
        ).willReturn(20)
        given(
            methodCall = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = account1.id!!,
                type = TransactionType.TRANSFER,
                status = listOf(TransactionStatus.COMPLETED),
                start = Instant.now().startOfTheYear(),
                end = Instant.now().endOfTheYear()
            )
        ).willReturn(200)

        // when/then
        assertThrows<AccountTransferNumberLimitException> {
            transferService.createTransfer(username = username, request = request)
        }

        verify(mock = accountRepository, mode = times(numInvocations = 2))
            .findAccountByCustomerUserUsernameAndType(
                username = any<String>(),
                type = eq(value = AccountType.PERSONAL_ACCOUNT)
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
        verify(mock = transactionRepository, mode = times(numInvocations = 3))
            .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
                from = eq(value = account1.id!!),
                type = eq(value = TransactionType.TRANSFER),
                status = eq(listOf(TransactionStatus.COMPLETED)),
                start = any<Instant>(),
                end = any<Instant>()
            )
    }

    @Test
    fun `should verify transfer successfully`() {
        // given
        val username = "test_username1"
        val request = VerifyTransferRequest(
            reference = UUID.randomUUID()
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID())

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = request.reference,
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token,
        )

        every { Generator.generateDecString() } returns tokenValue

        given(
            methodCall = transactionRepository.findTransactionByReference(reference = request.reference)
        ).willReturn(transfer)
        doNothing()
            .`when`(transactionTokenRepository)
            .deleteTransactionTokensByTransactionId(id = transfer.id!!)
        given(
            methodCall = tokenRepository.existsTokenByValue(token.value)
        ).willReturn(false)
        given(
            methodCall = tokenMapper.toEntity(value = tokenValue)
        ).willReturn(token)
        given(
            methodCall = tokenRepository.save(token)
        ).willReturn(token)
        given(
            methodCall = transactionTokenRepository.save(transactionToken)
        ).willReturn(transactionToken)
        doNothing()
            .`when`(mailSender)
            .sendEmail(
                email = EmailContent.VerifyTransfer(
                    to = transactionToken.transaction.from.customer.user.email,
                    username = transactionToken.transaction.from.customer.user.username,
                    token = transactionToken.token.value
                )
            )

        // when
        transferService.verifyTransfer(username = username, request = request)

        // then
        verify(mock = transactionRepository)
            .findTransactionByReference(reference = request.reference)
        verify(mock = transactionTokenRepository).deleteTransactionTokensByTransactionId(id = transfer.id!!)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = transactionTokenRepository).save(any<TransactionToken>())
    }

    @Test
    fun `should throw exception when verifying transfer for reference not found`() {
        // given
        val username = "test_username1"
        val request = VerifyTransferRequest(
            reference = UUID.randomUUID()
        )

        given(
            methodCall = transactionRepository.findTransactionByReference(reference = request.reference)
        ).willReturn(null)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transferService.verifyTransfer(username = username, request = request)
        }

        verify(mock = transactionRepository)
            .findTransactionByReference(reference = request.reference)
    }

    @Test
    fun `should throw exception when verifying transfer for username not found`() {
        // given
        val username = "test_username"
        val request = VerifyTransferRequest(
            reference = UUID.randomUUID()
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = "test_username1")
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), username = "test_username2")

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = request.reference,
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        given(
            methodCall = transactionRepository.findTransactionByReference(reference = request.reference)
        ).willReturn(transfer)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transferService.verifyTransfer(username = username, request = request)
        }

        verify(mock = transactionRepository)
            .findTransactionByReference(reference = request.reference)
    }

    @Test
    fun `should throw exception when verifying transfer for type mismatch`() {
        // given
        val username = "test_username1"
        val request = VerifyTransferRequest(
            reference = UUID.randomUUID()
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), username = "test_username2")

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = request.reference,
            amount = 1500.0,
            type = TransactionType.REQUEST,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        given(
            methodCall = transactionRepository.findTransactionByReference(reference = request.reference)
        ).willReturn(transfer)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transferService.verifyTransfer(username = username, request = request)
        }

        verify(mock = transactionRepository)
            .findTransactionByReference(reference = request.reference)
    }

    @Test
    fun `should throw exception when verifying transfer for status mismatch`() {
        // given
        val username = "test_username1"
        val request = VerifyTransferRequest(
            reference = UUID.randomUUID()
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), username = "test_username2")

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = request.reference,
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.COMPLETED,
            from = account1,
            to = account2
        )

        given(
            methodCall = transactionRepository.findTransactionByReference(reference = request.reference)
        ).willReturn(transfer)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transferService.verifyTransfer(username = username, request = request)
        }

        verify(mock = transactionRepository)
            .findTransactionByReference(reference = request.reference)
    }

    @Test
    fun `should confirm transfer successfully`() {
        // given
        val username = "test_username1"
        val request = ConfirmTransferRequest(
            token = "123567"
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID())

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = request.token)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token,
        )

        val savedTransfer = transactionToken.transaction.copy(
            status = TransactionStatus.COMPLETED,
            fees = 10.0,
            modifiedAt = Instant.now()
        )

        val savedAccount1 = account1.copy(
            balance = account1.balance - 1500.0 - 10.0,
            modifiedAt = Instant.now()
        )
        val savedAccount2 = account2.copy(
            balance = 1500.0,
            modifiedAt = Instant.now()
        )

        given(
            methodCall = transactionTokenRepository.findTransactionTokensByTokenValue(value = request.token)
        ).willReturn(transactionToken)
        given(
            methodCall = transactionMapper.updateEntity(transaction = any<Transaction>())
        ).willReturn(savedTransfer)
        given(
            methodCall = transactionRepository.save(savedTransfer)
        ).willReturn(savedTransfer)
        given(
            methodCall = accountMapper.updateEntity(account = account1.copy(balance = 25000.0 - 1500.0 - 10.0))
        ).willReturn(savedAccount1)
        given(
            methodCall = accountRepository.save(savedAccount1)
        ).willReturn(savedAccount1)
        given(
            methodCall = accountMapper.updateEntity(account = account2.copy(balance = 1500.0))
        ).willReturn(savedAccount2)
        given(
            methodCall = accountRepository.save(savedAccount2)
        ).willReturn(savedAccount2)
        doNothing()
            .`when`(transactionTokenRepository)
            .deleteTransactionTokensByTransactionId(id = transactionToken.transaction.id!!)
        doNothing()
            .`when`(mailSender)
            .sendEmail(email = any<EmailContent>())
        given(
            methodCall = transactionMapper.toResponse(transaction = savedTransfer)
        ).willReturn(TestResponseFactory.toTransactionResponse(transaction = savedTransfer))

        // when
        val result = transferService.confirmTransfer(username = username, request = request)

        // then
        verify(mock = transactionTokenRepository).findTransactionTokensByTokenValue(value = request.token)
        verify(mock = transactionRepository).save(any())
        verify(mock = accountRepository, mode = times(numInvocations = 2)).save(any())
        verify(mock = transactionTokenRepository).deleteTransactionTokensByTransactionId(id = transfer.id!!)

        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.TRANSFER)
        assert(value = result.status == TransactionStatus.COMPLETED)
        assert(value = result.fees == 10.0)
    }

    @Test
    fun `should throw exception when confirming transfer for verification code not found`() {
        // given
        val username = "test_username1"
        val request = ConfirmTransferRequest(
            token = "123567"
        )

        given(
            methodCall = transactionTokenRepository.findTransactionTokensByTokenValue(value = request.token)
        ).willReturn(null)

        // when/then
        assertThrows<VerificationCodeNotFoundException> {
            transferService.confirmTransfer(username = username, request = request)
        }

        verify(mock = transactionTokenRepository).findTransactionTokensByTokenValue(value = request.token)
    }

    @Test
    fun `should throw exception when confirming transfer for username not found`() {
        // given
        val username = "test_username1"
        val request = ConfirmTransferRequest(
            token = "123567"
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID())
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID())

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = request.token)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token,
        )

        given(
            methodCall = transactionTokenRepository.findTransactionTokensByTokenValue(value = request.token)
        ).willReturn(transactionToken)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transferService.confirmTransfer(username = username, request = request)
        }

        verify(mock = transactionTokenRepository).findTransactionTokensByTokenValue(value = request.token)
    }

    @Test
    fun `should throw exception when confirming transfer for verification code expired`() {
        // given
        val username = "test_username1"
        val request = ConfirmTransferRequest(
            token = "123567"
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID())

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = request.token)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token,
        )

        given(
            methodCall = transactionTokenRepository.findTransactionTokensByTokenValue(value = request.token)
        ).willReturn(transactionToken)
        given(methodCall = tokenMapper.isExpired(token = transactionToken.token)).willReturn(true)

        // when/then
        assertThrows<VerificationCodeExpiredException> {
            transferService.confirmTransfer(username = username, request = request)
        }

        verify(mock = transactionTokenRepository).findTransactionTokensByTokenValue(value = request.token)
    }

    @Test
    fun `should throw exception when confirming transfer for type mismatch`() {
        // given
        val username = "test_username1"
        val request = ConfirmTransferRequest(
            token = "123567"
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID())

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = 1500.0,
            type = TransactionType.REQUEST,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = request.token)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token,
        )

        given(
            methodCall = transactionTokenRepository.findTransactionTokensByTokenValue(value = request.token)
        ).willReturn(transactionToken)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transferService.confirmTransfer(username = username, request = request)
        }

        verify(mock = transactionTokenRepository).findTransactionTokensByTokenValue(value = request.token)
    }

    @Test
    fun `should throw exception when confirming transfer for status mismatch`() {
        // given
        val username = "test_username1"
        val request = ConfirmTransferRequest(
            token = "123567"
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID())

        val customer1 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user1)
        val customer2 = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user2)

        val account1Name = "Test Account 1"
        val account2Name = "Test Account 2"

        val account1 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "1234567890",
            name = account1Name,
            balance = 25000.0,
            customer = customer1
        )
        val account2 = TestDataFactory.createAccount(
            id = UUID.randomUUID(),
            number = "0987654321",
            name = account2Name,
            customer = customer2
        )

        val reference = UUID.randomUUID()

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            reference = reference,
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.COMPLETED,
            from = account1,
            to = account2
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = request.token)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token,
        )

        given(
            methodCall = transactionTokenRepository.findTransactionTokensByTokenValue(value = request.token)
        ).willReturn(transactionToken)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transferService.confirmTransfer(username = username, request = request)
        }

        verify(mock = transactionTokenRepository).findTransactionTokensByTokenValue(value = request.token)
    }

    @Test
    fun `should throw exception when confirming transfer for account insufficient funds`() {
        // given
        val username = "test_username1"
        val request = ConfirmTransferRequest(
            token = "123567"
        )

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID())

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

        val transfer = TestDataFactory.createTransaction(
            id = UUID.randomUUID(),
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            from = account1,
            to = account2
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = request.token)

        val transactionToken = TestDataFactory.createTransactionToken(
            transaction = transfer,
            token = token,
        )

        given(
            methodCall = transactionTokenRepository.findTransactionTokensByTokenValue(value = request.token)
        ).willReturn(transactionToken)

        // when/then
        assertThrows<AccountInsufficientFundsException> {
            transferService.confirmTransfer(username = username, request = request)
        }

        verify(mock = transactionTokenRepository).findTransactionTokensByTokenValue(value = request.token)
    }
}

