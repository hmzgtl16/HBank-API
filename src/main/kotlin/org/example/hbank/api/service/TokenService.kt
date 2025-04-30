package org.example.hbank.api.service

import org.example.hbank.api.model.*
import org.example.hbank.api.repository.*
import org.example.hbank.api.utility.Generator.generateDecString
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
@Transactional
class TokenService(
    private val clock: Clock,
    private val tokenRepository: TokenRepository,
    private val userVerifyEmailTokenRepository: UserVerifyEmailTokenRepository,
    private val userResetPasswordTokenRepository: UserResetPasswordTokenRepository,
    private val accountTokenRepository: AccountTokenRepository
) {

    fun createVerifyEmailToken(user: User, token: Token): UserVerifyEmailToken {
        val verifyEmailTokenEntity = UserVerifyEmailToken(token = token, user = user)
        return userVerifyEmailTokenRepository.save(verifyEmailTokenEntity)
    }

    fun createResetPasswordToken(user: User, token: Token): UserResetPasswordToken {
        val userResetPasswordToken = UserResetPasswordToken(token = token, user = user)
        return userResetPasswordTokenRepository.save(userResetPasswordToken)
    }

    fun getOrCreateAccountToken(account: Account): AccountToken =
        accountTokenRepository.findAccountTokenByAccount(account = account)
            ?: createAccountToken(account = account)

    private fun createAccountToken(account: Account): AccountToken {
        val token = createToken()
        val accountToken = AccountToken(token = token, account = account)
        return accountTokenRepository.save(accountToken)
    }

    fun createToken(): Token {
        val value = generateTokenValue()
        val token = Token(
            value = value,
            created = Instant.now(clock)
        )
        return tokenRepository.save(token)
    }

    fun deleteVerifyEmailTokens(user: User) {
        userVerifyEmailTokenRepository.deleteUserVerifyEmailTokensByUser(user = user)
    }

    fun deleteResetPasswordTokens(user: User) {
        userResetPasswordTokenRepository.deleteUserResetPasswordTokensByUser(user = user)
    }

    fun getVerifyEmailToken(value: String): UserVerifyEmailToken? =
        userVerifyEmailTokenRepository.findUserVerifyEmailTokenByTokenValue(value)

    fun getResetPasswordToken(value: String): UserResetPasswordToken? =
        userResetPasswordTokenRepository.findUserResetPasswordTokenByTokenValue(value)

    fun getAccountToken(value: String): AccountToken? =
        accountTokenRepository.findAccountTokenByTokenValue(value)

    fun generateTokenValue(): String {
        var generatedValue: String
        do {
            generatedValue = generateDecString(length = 6)
        } while (tokenRepository.existsTokenByValue(value = generatedValue))

        return generatedValue
    }

    fun generateAccountTokenValue(): String {
        var generatedValue: String
        do {
            generatedValue = UUID.randomUUID().toString()
        } while (tokenRepository.existsTokenByValue(value = generatedValue))

        return generatedValue
    }

    fun isVerifyEmailTokenExpired(created: Instant): Boolean = Duration
        .between(created, Instant.now(clock)).toMinutes() > 10

    fun isResetPasswordTokenExpired(created: Instant): Boolean = Duration
        .between(created, Instant.now(clock)).toMinutes() > 10

}

