package org.example.hbank.api.service

import org.example.hbank.api.mapper.TransactionMapper
import org.example.hbank.api.repository.AccountRepository
import org.example.hbank.api.repository.TransactionRepository
import org.example.hbank.api.util.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

class TransactionServiceTest {

    private val accountRepository = mock<AccountRepository>()
    private val transactionRepository = mock<TransactionRepository>()
    private val transactionMapper = mock<TransactionMapper>()

    private val transactionService = TransactionServiceImpl(
        accountRepository = accountRepository,
        transactionRepository = transactionRepository,
        transactionMapper = transactionMapper
    )

    @Test
    fun `should list transactions for username with pagination`() {
        // given
        val username1 = "test_username1"
        val username2 = "test_username2"

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username1)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username2)

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

        val transactions = listOf(
            TestDataFactory.createTransaction(
                id = UUID.randomUUID(),
                from = account1,
                to = account2
            ),
            TestDataFactory.createTransaction(
                id = UUID.randomUUID(),
                from = account1,
                to = account2
            ),
            TestDataFactory.createTransaction(
                id = UUID.randomUUID(),
                from = account2,
                to = account1
            ),
            TestDataFactory.createTransaction(
                id = UUID.randomUUID(),
                from = account2,
                to = account1
            )
        )

        val pageable = PageRequest.of(0, 3)

        given(methodCall = accountRepository.existsAccountByCustomerUserUsername(username1)).willReturn(true)
        val page = PageImpl(transactions, pageable, transactions.size.toLong())
        given(
            methodCall = transactionRepository.findDistinctTransactionsByFromCustomerUserUsernameOrToCustomerUserUsernameAndStatusIn(
                username = username1,
                status = listOf(TransactionStatus.COMPLETED),
                pageable = pageable
            )
        ).willReturn(page)
        given(transactionMapper.toResponse(any())).willAnswer {
            TestResponseFactory.toTransactionResponse(transaction = it.getArgument(0))
        }

        // when
        val result = transactionService
            .getTransactions(username = username1, pageable = pageable)

        // then
        verify(accountRepository).existsAccountByCustomerUserUsername(username = username1)
        verify(transactionRepository)
            .findDistinctTransactionsByFromCustomerUserUsernameOrToCustomerUserUsernameAndStatusIn(
                username = username1,
                status = listOf(TransactionStatus.COMPLETED),
                pageable = pageable
            )

        assert(value = result.content.size == 4)
        assert(value = result.totalPages == 2)
        assert(value = result.content[0].from.name == account1Name && result.content[0].to.name == account2Name)
        assert(value = result.content[1].from.name == account1Name && result.content[0].to.name == account2Name)
        assert(value = result.content[2].from.name == account2Name && result.content[2].to.name == account1Name)
        assert(value = result.content[3].from.name == account2Name && result.content[3].to.name == account1Name)
    }

    @Test
    fun `should get transaction by reference successfully`() {
        // given
        val username1 = "test_username1"
        val username2 = "test_username2"

        val user1 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username1)
        val user2 = TestDataFactory.createUser(id = UUID.randomUUID(), username = username2)

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
            from = account1,
            to = account2
        )

        given(methodCall = transactionRepository.findTransactionByReference(reference = reference))
            .willReturn(transaction)
        given(transactionMapper.toResponse(transaction = transaction))
            .willReturn(TestResponseFactory.toTransactionResponse(transaction = transaction))

        // when
        val result = transactionService
            .getTransaction(reference = reference)

        verify(mock = transactionRepository).findTransactionByReference(reference = reference)
        assert(value = result.reference == reference)
        assert(value = result.type == TransactionType.TRANSFER)
        assert(value = result.status == TransactionStatus.COMPLETED)
    }

    @Test
    fun `should throw exception when no transaction for reference`() {
        // given
        val reference = UUID.randomUUID()

        given(methodCall = transactionRepository.findTransactionByReference(reference = reference))
            .willReturn(null)

        // when/then
        assertThrows<TransactionNotFoundException> {
            transactionService.getTransaction(reference = reference)
        }

        verify(mock = transactionRepository).findTransactionByReference(reference = reference)
    }
}