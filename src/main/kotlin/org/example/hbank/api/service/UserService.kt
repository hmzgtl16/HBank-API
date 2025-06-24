package org.example.hbank.api.service

import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.mapper.UserMapper
import org.example.hbank.api.model.User
import org.example.hbank.api.model.UserRole
import org.example.hbank.api.model.UserRoleRepository
import org.example.hbank.api.model.UserToken
import org.example.hbank.api.repository.RoleRepository
import org.example.hbank.api.repository.TokenRepository
import org.example.hbank.api.repository.UserRepository
import org.example.hbank.api.repository.UserTokenRepository
import org.example.hbank.api.request.*
import org.example.hbank.api.util.EmailAlreadyExistException
import org.example.hbank.api.util.EmailAlreadyVerifiedException
import org.example.hbank.api.util.EmailContent
import org.example.hbank.api.util.EmailNotFoundException
import org.example.hbank.api.util.EmailNotVerifiedException
import org.example.hbank.api.util.Generator
import org.example.hbank.api.util.RoleNotFoundException
import org.example.hbank.api.util.Roles
import org.example.hbank.api.util.TokenType
import org.example.hbank.api.util.UsernameAlreadyExistException
import org.example.hbank.api.util.VerificationCodeExpiredException
import org.example.hbank.api.util.VerificationCodeNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserService {
    fun registerUser(request: RegisterUserRequest)
    fun sendVerifyEmail(request: SendVerifyEmailRequest)
    fun verifyEmail(request: VerifyEmailRequest)
    fun forgotPassword(request: ForgotPasswordRequest)
    fun resetPassword(request: ResetPasswordRequest)
}

@Service
class UserServiceImpl(
    private val roleRepository: RoleRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val userTokenRepository: UserTokenRepository,
    private val tokenMapper: TokenMapper,
    private val userMapper: UserMapper,
    private val mailSender: MailSender,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    @Transactional
    override fun registerUser(request: RegisterUserRequest) {

        validateNewUser(request = request)

        val user = userMapper
            .toEntity(request = request)
            .copy(password = passwordEncoder.encode(request.password))
            .also(block = userRepository::save)

        addRoleUser(user = user)

        val userToken = newUserToken(user = user, type = TokenType.VERIFY_EMAIL)

        mailSender.sendEmail(
            EmailContent.VerifyEmail(
                to = userToken.user.email,
                username = userToken.user.username,
                token = userToken.token.value
            )
        )
    }

    @Transactional
    override fun sendVerifyEmail(request: SendVerifyEmailRequest) {

        val user = userRepository.findUserByEmail(email = request.email)
            ?: throw EmailNotFoundException()

        if (user.enabled)
            throw EmailAlreadyVerifiedException()

        val userToken = newUserToken(user = user, type = TokenType.VERIFY_EMAIL)

        mailSender.sendEmail(
            EmailContent.VerifyEmail(
                to = userToken.user.email,
                username = userToken.user.username,
                token = userToken.token.value
            )
        )
    }

    @Transactional
    override fun verifyEmail(request: VerifyEmailRequest) {
        val userToken = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode)
            ?: throw VerificationCodeNotFoundException()

        if (userToken.user.enabled)
            throw EmailAlreadyVerifiedException()

        if (tokenMapper.isExpired(token = userToken.token))
            throw VerificationCodeExpiredException()

        userRepository.save(userToken.user.copy(enabled = true))
        userTokenRepository.deleteUserTokensByUserIdAndType(id = userToken.user.id!!, type = TokenType.VERIFY_EMAIL)
    }

    @Transactional
    override fun forgotPassword(request: ForgotPasswordRequest) {

        val user = userRepository.findUserByEmail(email = request.email)
            ?: throw EmailNotFoundException()

        if (!user.enabled)
            throw EmailNotVerifiedException()

        val userToken = newUserToken(user = user, type = TokenType.RESET_PASSWORD)

        mailSender.sendEmail(
            EmailContent.ForgotPassword(
                to = userToken.user.email,
                username = userToken.user.username,
                token = userToken.token.value
            )
        )
    }

    @Transactional
    override fun resetPassword(request: ResetPasswordRequest) {
        val userToken = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode)
            ?: throw VerificationCodeNotFoundException()

        if (!userToken.user.enabled)
            throw EmailNotVerifiedException()

        if (tokenMapper.isExpired(token = userToken.token))
            throw VerificationCodeExpiredException()

        userRepository.save(userToken.user.copy(password = passwordEncoder.encode(request.password)))
        userTokenRepository.deleteUserTokensByUserIdAndType(id = userToken.user.id!!, type = TokenType.RESET_PASSWORD)
    }

    private fun newUserToken(user: User, type: TokenType): UserToken {
        userTokenRepository.deleteUserTokensByUserIdAndType(id = user.id!!, type = type)
        val token = tokenRepository.save(tokenMapper.toEntity(value = generateTokenValue()))
        return userTokenRepository.save(UserToken(user = user, token = token, type = type))
    }

    private fun addRoleUser(user: User) {
        val role = roleRepository.findRoleByName(name = Roles.ROLE_USER)
            ?: throw RoleNotFoundException()
        userRoleRepository.save(UserRole(user = user, role = role))
    }

    private fun validateNewUser(request: RegisterUserRequest) {

        if (userRepository.existsUserByUsername(username = request.username))
            throw UsernameAlreadyExistException()

        if (userRepository.existsUserByEmail(email = request.email))
            throw EmailAlreadyExistException()
    }

    private fun generateTokenValue(): String {
        var value: String
        do {
            value = Generator.generateDecString()
        } while (tokenRepository.existsTokenByValue(value = value))

        return value
    }
}

