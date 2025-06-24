package org.example.hbank.api.service

import org.example.hbank.api.mapper.AccountMapper
import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.AccountToken
import org.example.hbank.api.repository.AccountRepository
import org.example.hbank.api.repository.AccountTokenRepository
import org.example.hbank.api.repository.TokenRepository
import org.example.hbank.api.response.PersonalAccountResponse
import org.example.hbank.api.util.*
import org.example.hbank.api.util.Generator
import org.springframework.stereotype.Service

interface AccountService {
    fun getPersonalAccount(username: String): PersonalAccountResponse
}

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountTokenRepository: AccountTokenRepository,
    private val tokenRepository: TokenRepository,
    private val accountMapper: AccountMapper,
    private val tokenMapper: TokenMapper
) : AccountService {

    override fun getPersonalAccount(username: String): PersonalAccountResponse {
        val personalAccount = accountRepository
            .findAccountByCustomerUserUsernameAndType(username = username, type = AccountType.PERSONAL_ACCOUNT)
            ?: throw AccountNotFoundException()

        createAccountTokenIfNotExists(account = personalAccount)

        return accountMapper.toPersonalAccountResponse(account = personalAccount)
    }

    private fun createAccountTokenIfNotExists(account: Account) {
        if (accountTokenRepository.existsAccountTokenByAccountId(id = account.id!!)) return

        val token = tokenRepository.save(tokenMapper.toEntity(value = generateTokenValue()))

        accountTokenRepository.save(AccountToken(account = account, token = token))
    }

    fun generateTokenValue(): String {
        var value: String
        do {
            value = Generator.generateHexString(length = 32)
        } while (tokenRepository.existsTokenByValue(value = value))
        return value
    }
}
