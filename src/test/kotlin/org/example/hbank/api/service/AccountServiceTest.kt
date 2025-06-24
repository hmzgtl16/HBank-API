package org.example.hbank.api.service

import org.example.hbank.api.mapper.AccountMapper
import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.repository.AccountRepository
import org.example.hbank.api.repository.AccountTokenRepository
import org.example.hbank.api.repository.TokenRepository
import org.example.hbank.api.util.AccountNotFoundException
import org.example.hbank.api.util.AccountType
import org.example.hbank.api.util.TestDataFactory
import org.example.hbank.api.util.TestResponseFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Clock
import java.util.*

class AccountServiceTest {

    private val accountRepository = mock<AccountRepository>()
    private val accountTokenRepository = mock<AccountTokenRepository>()
    private val tokenRepository = mock<TokenRepository>()
    private val accountMapper = mock<AccountMapper>()
    private val tokenMapper = mock<TokenMapper>()

    private val accountService: AccountService = AccountServiceImpl(
        accountRepository = accountRepository,
        accountTokenRepository = accountTokenRepository,
        tokenRepository = tokenRepository,
        accountMapper = accountMapper,
        tokenMapper = tokenMapper
    )

    @Test
    fun `should get personal account by username successfully`() {
        // given
        val username = "test_username"
        val user = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val customer = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user)
        val accountId = UUID.randomUUID()
        val account = TestDataFactory.createAccount(id = accountId, customer = customer)

        given(
            methodCall = accountRepository
                .findAccountByCustomerUserUsernameAndType(username = username, type = AccountType.PERSONAL_ACCOUNT)
        ).willReturn(account)
        given(methodCall = accountTokenRepository.existsAccountTokenByAccountId(accountId)).willReturn(true)
        given(methodCall = accountMapper.toPersonalAccountResponse(account))
            .willReturn(TestResponseFactory.toPersonalAccountResponse(account = account))

        // when
        val result = accountService.getPersonalAccount(username = username)

        // then
        verify(mock = accountRepository)
            .findAccountByCustomerUserUsernameAndType(username = username, type = AccountType.PERSONAL_ACCOUNT)

        assert(value = result.number == account.number)
        assert(value = result.name == account.name)
    }

    @Test
    fun `should throw exception when non-existent personal account`() {
        // given
        val username = "test_username"

        given(
            methodCall = accountRepository
                .findAccountByCustomerUserUsernameAndType(username = username, type = AccountType.PERSONAL_ACCOUNT)
        ).willReturn(null)

        // then
        assertThrows<AccountNotFoundException> {
            accountService.getPersonalAccount(username = username)
        }

        verify(mock = accountRepository)
            .findAccountByCustomerUserUsernameAndType(username = username, type = AccountType.PERSONAL_ACCOUNT)
    }
}
