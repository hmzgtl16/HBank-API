package org.example.hbank.api.service

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.example.hbank.api.mapper.CustomerMapper
import org.example.hbank.api.mapper.TokenMapper
import org.example.hbank.api.mapper.UserMapper
import org.example.hbank.api.model.*
import org.example.hbank.api.repository.*
import org.example.hbank.api.request.ForgotPasswordRequest
import org.example.hbank.api.request.RegisterUserRequest
import org.example.hbank.api.request.ResetPasswordRequest
import org.example.hbank.api.request.SendVerifyEmailRequest
import org.example.hbank.api.request.VerifyEmailRequest
import org.example.hbank.api.util.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*


class UserServiceTest {

    private val roleRepository = mock<RoleRepository>()
    private val tokenRepository = mock<TokenRepository>()
    private val userRepository = mock<UserRepository>()
    private val userRoleRepository = mock<UserRoleRepository>()
    private val userTokenRepository = mock<UserTokenRepository>()
    private val tokenMapper = mock<TokenMapper>()
    private val userMapper = mock<UserMapper>()
    private val mailSender = mock<MailSender>()
    private val passwordEncoder = mock<PasswordEncoder>()

    private var userService: UserService = UserServiceImpl(
        roleRepository,
        tokenRepository,
        userRepository,
        userRoleRepository,
        userTokenRepository,
        tokenMapper,
        userMapper,
        mailSender,
        passwordEncoder
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
    fun `should register user successfully`() {
        // given
        val request = RegisterUserRequest(
            username = "test_username",
            email = "test@example.com",
            password = "password"
        )

        val encodedPassword = "encoded_password"
        val user = TestDataFactory.createUser(
            id = UUID.randomUUID(),
            username = request.username,
            email = request.email,
            password = encodedPassword
        )

        val role = TestDataFactory.createRole(id = UUID.randomUUID(), name = Roles.ROLE_USER)
        val userRole = TestDataFactory.createUserRole(user = user, role = role)

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)
        val userToken = TestDataFactory.createUserToken(
            user = user,
            token = token,
            type = TokenType.VERIFY_EMAIL
        )

        every { Generator.generateDecString() } returns tokenValue

        given(methodCall = userRepository.existsUserByUsername(username = request.username)).willReturn(false)
        given(methodCall = userRepository.existsUserByEmail(email = request.email)).willReturn(false)
        given(methodCall = userMapper.toEntity(request = request)).willReturn(user)
        given(methodCall = passwordEncoder.encode(request.password)).willReturn(encodedPassword)
        given(methodCall = userRepository.save(user)).willReturn(user)
        given(methodCall = roleRepository.findRoleByName(name = Roles.ROLE_USER)).willReturn(role)
        given(methodCall = userRoleRepository.save(userRole)).willReturn(userRole)
        doNothing().`when`(userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = user.id!!, type = TokenType.VERIFY_EMAIL)
        given(methodCall = tokenRepository.existsTokenByValue(token.value)).willReturn(false)
        given(methodCall = tokenMapper.toEntity(value = tokenValue)).willReturn(token)
        given(methodCall = tokenRepository.save(token)).willReturn(token)
        given(methodCall = userTokenRepository.save(userToken)).willReturn(userToken)
        doNothing().`when`(mailSender).sendEmail(
            EmailContent.VerifyEmail(
                to = userToken.user.email,
                username = userToken.user.username,
                token = userToken.token.value
            )
        )

        // when
        userService.registerUser(request = request)

        // then
        verify(mock = userRepository).existsUserByUsername(username = request.username)
        verify(mock = userRepository).existsUserByEmail(email = request.email)
        verify(mock = passwordEncoder).encode(request.password)
        verify(mock = userRepository).save(any<User>())
        verify(mock = roleRepository).findRoleByName(name = Roles.ROLE_USER)
        verify(mock = userRoleRepository).save(any<UserRole>())
        verify(mock = userTokenRepository).deleteUserTokensByUserIdAndType(
            id = user.id!!,
            type = TokenType.VERIFY_EMAIL
        )
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = userTokenRepository).save(any<UserToken>())
    }

    @Test
    fun `should throw exception when username already exists`() {
        // given
        val request = RegisterUserRequest(
            username = "test_username",
            email = "test@example.com",
            password = "password"
        )

        given(methodCall = userRepository.existsUserByUsername(username = request.username)).willReturn(true)

        // when/then
        assertThrows<UsernameAlreadyExistException> {
            userService.registerUser(request = request)
        }

        verify(mock = userRepository).existsUserByUsername(username = request.username)
        verify(mock = userRepository, mode = never()).existsUserByEmail(email = request.email)
        verify(mock = passwordEncoder, mode = never()).encode(request.password)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = roleRepository, mode = never()).findRoleByName(name = Roles.ROLE_USER)
        verify(mock = userRoleRepository, mode = never()).save(any<UserRole>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
        verify(mock = tokenRepository, mode = never()).existsTokenByValue(value = any<String>())
        verify(mock = tokenRepository, mode = never()).save(any<Token>())
        verify(mock = userTokenRepository, mode = never()).save(any<UserToken>())
    }

    @Test
    fun `should throw exception when email already exists`() {
        // given
        val request = RegisterUserRequest(
            username = "test_username",
            email = "test@example.com",
            password = "password"
        )

        given(methodCall = userRepository.existsUserByUsername(username = request.username)).willReturn(false)
        given(methodCall = userRepository.existsUserByEmail(email = request.email)).willReturn(true)

        // when/then
        assertThrows<EmailAlreadyExistException> {
            userService.registerUser(request = request)
        }

        verify(mock = userRepository).existsUserByUsername(username = request.username)
        verify(mock = userRepository).existsUserByEmail(email = request.email)
        verify(mock = passwordEncoder, mode = never()).encode(request.password)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = roleRepository, mode = never()).findRoleByName(name = Roles.ROLE_USER)
        verify(mock = userRoleRepository, mode = never()).save(any<UserRole>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
        verify(mock = tokenRepository, mode = never()).existsTokenByValue(value = any<String>())
        verify(mock = tokenRepository, mode = never()).save(any<Token>())
        verify(mock = userTokenRepository, mode = never()).save(any<UserToken>())
    }

    @Test
    fun `should throw exception when non-existent role`() {
        // given
        val request = RegisterUserRequest(
            username = "test_username",
            email = "test@example.com",
            password = "password"
        )

        val encodedPassword = "encoded_password"
        val user = TestDataFactory.createUser(
            id = UUID.randomUUID(),
            username = request.username,
            email = request.email,
            password = encodedPassword
        )

        given(methodCall = userRepository.existsUserByUsername(username = request.username)).willReturn(false)
        given(methodCall = userRepository.existsUserByEmail(email = request.email)).willReturn(false)
        given(methodCall = userMapper.toEntity(request = request)).willReturn(user)
        given(methodCall = passwordEncoder.encode(request.password)).willReturn(encodedPassword)
        given(methodCall = userRepository.save(user)).willReturn(user)
        given(methodCall = roleRepository.findRoleByName(name = Roles.ROLE_USER)).willReturn(null)

        // when/then
        assertThrows<RoleNotFoundException> {
            userService.registerUser(request = request)
        }

        verify(mock = userRepository).existsUserByUsername(username = request.username)
        verify(mock = userRepository).existsUserByEmail(email = request.email)
        verify(mock = passwordEncoder).encode(request.password)
        verify(mock = userRepository).save(user)
        verify(mock = roleRepository).findRoleByName(name = Roles.ROLE_USER)
        verify(mock = userRoleRepository, mode = never()).save(any<UserRole>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
        verify(mock = tokenRepository, mode = never()).existsTokenByValue(value = any<String>())
        verify(mock = tokenRepository, mode = never()).save(any<Token>())
        verify(mock = userTokenRepository, mode = never()).save(any<UserToken>())
    }

    @Test
    fun `should send verify email successfully`() {
        // given
        val request = SendVerifyEmailRequest(email = "test@example.com")

        val user = TestDataFactory.createUser(
            id = UUID.randomUUID(),
            username = "test_username",
            email = request.email,
            password = "encoded_password",
            enabled = false
        )

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)
        val userToken = TestDataFactory.createUserToken(
            user = user,
            token = token,
            type = TokenType.VERIFY_EMAIL
        )

        every { Generator.generateDecString() } returns tokenValue

        given(methodCall = userRepository.findUserByEmail(email = request.email)).willReturn(user)
        doNothing().`when`(userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = user.id!!, type = TokenType.VERIFY_EMAIL)
        given(methodCall = tokenRepository.existsTokenByValue(token.value)).willReturn(false)
        given(methodCall = tokenMapper.toEntity(value = tokenValue)).willReturn(token)
        given(methodCall = tokenRepository.save(token)).willReturn(token)
        given(methodCall = userTokenRepository.save(userToken)).willReturn(userToken)
        doNothing().`when`(mailSender).sendEmail(
            EmailContent.VerifyEmail(
                to = userToken.user.email,
                username = userToken.user.username,
                token = userToken.token.value
            )
        )

        // when
        userService.sendVerifyEmail(request = request)

        verify(mock = userRepository).findUserByEmail(email = request.email)
        verify(mock = userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = user.id!!, type = TokenType.VERIFY_EMAIL)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = userTokenRepository).save(any<UserToken>())
    }

    @Test
    fun `should throw exception when email not found`() {
        // given
        val request = SendVerifyEmailRequest(email = "test@example.com")

        given(methodCall = userRepository.findUserByEmail(email = request.email)).willReturn(null)

        // when/then
        assertThrows<EmailNotFoundException> {
            userService.sendVerifyEmail(request = request)
        }

        verify(mock = userRepository).findUserByEmail(email = request.email)
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
        verify(mock = tokenRepository, mode = never()).existsTokenByValue(value = any<String>())
        verify(mock = tokenRepository, mode = never()).save(any<Token>())
        verify(mock = userTokenRepository, mode = never()).save(any<UserToken>())
    }

    @Test
    fun `should throw exception when email already verified`() {
        // given
        val request = SendVerifyEmailRequest(email = "test@example.com")

        val user = TestDataFactory.createUser(
            id = UUID.randomUUID(),
            username = "test_username",
            email = request.email,
            password = "encoded_password",
            enabled = true
        )

        given(methodCall = userRepository.findUserByEmail(email = request.email)).willReturn(user)

        // when/then
        assertThrows<EmailAlreadyVerifiedException> {
            userService.sendVerifyEmail(request = request)
        }

        verify(mock = userRepository).findUserByEmail(email = request.email)
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
        verify(mock = tokenRepository, mode = never()).existsTokenByValue(value = any<String>())
        verify(mock = tokenRepository, mode = never()).save(any<Token>())
        verify(mock = userTokenRepository, mode = never()).save(any<UserToken>())

    }

    @Test
    fun `should verify email successfully`() {
        // given
        val verificationCode = "345645"
        val request = VerifyEmailRequest(verificationCode = verificationCode)

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = verificationCode)

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), enabled = false)

        val userToken = TestDataFactory.createUserToken(user = user, token = token, type = TokenType.VERIFY_EMAIL)

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(userToken)
        given(methodCall = tokenMapper.isExpired(token = userToken.token)).willReturn(false)
        given(methodCall = userRepository.save(userToken.user.copy(enabled = true)))
            .willReturn(userToken.user.copy(enabled = true))
        doNothing().`when`(userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = userToken.user.id!!, type = TokenType.VERIFY_EMAIL)

        // when
            userService.verifyEmail(request = request)

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = userRepository).save(userToken.user.copy(enabled = true))
        verify(mock = userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = userToken.user.id!!, type = TokenType.VERIFY_EMAIL)
    }

    @Test
    fun `should throw exception when token not found`() {
        // given
        val verificationCode = "345645"
        val request = VerifyEmailRequest(verificationCode = verificationCode)

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(null)

        // when/then
        assertThrows<VerificationCodeNotFoundException> {
            userService.verifyEmail(request = request)
        }

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
    }

    @Test
    fun `should throw exception when user enabled`() {
        // given
        val verificationCode = "345645"
        val request = VerifyEmailRequest(verificationCode = verificationCode)

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = verificationCode)

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), enabled = true)

        val userToken = TestDataFactory.createUserToken(user = user, token = token, type = TokenType.VERIFY_EMAIL)

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(userToken)

        // when/then
        assertThrows<EmailAlreadyVerifiedException> {
            userService.verifyEmail(request = request)
        }

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
    }

    @Test
    fun `should throw exception when token expired`() {
        // given
        val verificationCode = "345645"
        val request = VerifyEmailRequest(verificationCode = verificationCode)

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = verificationCode)

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), enabled = false)

        val userToken = TestDataFactory.createUserToken(user = user, token = token, type = TokenType.VERIFY_EMAIL)

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(userToken)
        given(methodCall = tokenMapper.isExpired(token = userToken.token)).willReturn(true)

        // when/then
        assertThrows<VerificationCodeExpiredException> {
            userService.verifyEmail(request = request)
        }

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
    }

    @Test
    fun `should forgot password successfully`() {
        // given
        val request = ForgotPasswordRequest(email = "test@example.com")

        val user = TestDataFactory.createUser(
            id = UUID.randomUUID(),
            username = "test_username",
            email = request.email,
            password = "encoded_password",
            enabled = true
        )

        val tokenValue = "123567"
        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = tokenValue)
        val userToken = TestDataFactory.createUserToken(
            user = user,
            token = token,
            type = TokenType.RESET_PASSWORD
        )

        every { Generator.generateDecString() } returns tokenValue

        given(methodCall = userRepository.findUserByEmail(email = request.email)).willReturn(user)
        doNothing().`when`(userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = user.id!!, type = TokenType.RESET_PASSWORD)
        given(methodCall = tokenRepository.existsTokenByValue(token.value)).willReturn(false)
        given(methodCall = tokenMapper.toEntity(value = tokenValue)).willReturn(token)
        given(methodCall = tokenRepository.save(token)).willReturn(token)
        given(methodCall = userTokenRepository.save(userToken)).willReturn(userToken)
        doNothing().`when`(mailSender).sendEmail(
            EmailContent.ForgotPassword(
                to = userToken.user.email,
                username = userToken.user.username,
                token = userToken.token.value
            )
        )

        // when
        userService.forgotPassword(request = request)

        verify(mock = userRepository).findUserByEmail(email = request.email)
        verify(mock = userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = user.id!!, type = TokenType.RESET_PASSWORD)
        verify(mock = tokenRepository).existsTokenByValue(value = tokenValue)
        verify(mock = tokenRepository).save(any<Token>())
        verify(mock = userTokenRepository).save(any<UserToken>())
    }

    @Test
    fun `should throw exception when email not exist`() {
        // given
        val request = ForgotPasswordRequest(email = "test@example.com")

        given(methodCall = userRepository.findUserByEmail(email = request.email)).willReturn(null)

        // when/then
        assertThrows<EmailNotFoundException> {
            userService.forgotPassword(request = request)
        }

        verify(mock = userRepository).findUserByEmail(email = request.email)
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
        verify(mock = tokenRepository, mode = never()).existsTokenByValue(value = any<String>())
        verify(mock = tokenRepository, mode = never()).save(any<Token>())
        verify(mock = userTokenRepository, mode = never()).save(any<UserToken>())
    }

    @Test
    fun `should throw exception when email not verified`() {
        // given
        val request = ForgotPasswordRequest(email = "test@example.com")

        val user = TestDataFactory.createUser(
            id = UUID.randomUUID(),
            username = "test_username",
            email = request.email,
            password = "encoded_password",
            enabled = false
        )

        given(methodCall = userRepository.findUserByEmail(email = request.email)).willReturn(user)

        // when/then
        assertThrows<EmailNotVerifiedException> {
            userService.forgotPassword(request = request)
        }

        verify(mock = userRepository).findUserByEmail(email = request.email)
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
        verify(mock = tokenRepository, mode = never()).existsTokenByValue(value = any<String>())
        verify(mock = tokenRepository, mode = never()).save(any<Token>())
        verify(mock = userTokenRepository, mode = never()).save(any<UserToken>())
    }

    @Test
    fun `should reset password successfully`() {
        // given
        val verificationCode = "345645"
        val request = ResetPasswordRequest(
            verificationCode = verificationCode,
            password = "password"
        )

        val encodedPassword = "encoded_password"

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = verificationCode)

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), enabled = true)

        val userToken = TestDataFactory.createUserToken(user = user, token = token, type = TokenType.RESET_PASSWORD)

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(userToken)
        given(methodCall = tokenMapper.isExpired(token = userToken.token)).willReturn(false)
        given(methodCall = passwordEncoder.encode(request.password)).willReturn(encodedPassword)
        given(methodCall = userRepository.save(userToken.user.copy()))
            .willReturn(userToken.user.copy(enabled = true))
        doNothing().`when`(userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = userToken.user.id!!, type = TokenType.RESET_PASSWORD)

        // when
            userService.resetPassword(request = request)

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = passwordEncoder).encode(request.password)
        verify(mock = userRepository).save(userToken.user.copy(password = encodedPassword))
        verify(mock = userTokenRepository)
            .deleteUserTokensByUserIdAndType(id = userToken.user.id!!, type = TokenType.RESET_PASSWORD)
    }

    @Test
    fun `should throw exception when verification code not found`() {
        // given
        val verificationCode = "345645"
        val request = ResetPasswordRequest(
            verificationCode = verificationCode,
            password = "password"
        )

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(null)

        // when/then
        assertThrows<VerificationCodeNotFoundException> {
            userService.resetPassword(request = request)
        }

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
    }

    @Test
    fun `should throw exception when user not enabled`() {
        // given
        val verificationCode = "345645"
        val request = ResetPasswordRequest(
            verificationCode = verificationCode,
            password = "password"
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = verificationCode)

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), enabled = false)

        val userToken = TestDataFactory.createUserToken(user = user, token = token, type = TokenType.RESET_PASSWORD)

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(userToken)

        // when/then
        assertThrows<EmailNotVerifiedException> {
            userService.resetPassword(request = request)
        }

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
    }

    @Test
    fun `should throw exception when verification code expired`() {
        // given
        val verificationCode = "345645"
        val request = ResetPasswordRequest(
            verificationCode = verificationCode,
            password = "password"
        )

        val token = TestDataFactory.createToken(id = UUID.randomUUID(), value = verificationCode)

        val user = TestDataFactory.createUser(id = UUID.randomUUID(), enabled = true)

        val userToken = TestDataFactory.createUserToken(user = user, token = token, type = TokenType.RESET_PASSWORD)

        given(methodCall = userTokenRepository.findUserTokenByTokenValue(value = request.verificationCode))
            .willReturn(userToken)
        given(methodCall = tokenMapper.isExpired(token = userToken.token)).willReturn(true)

        // when/then
        assertThrows<VerificationCodeExpiredException> {
            userService.resetPassword(request = request)
        }

        verify(mock = userTokenRepository).findUserTokenByTokenValue(value = request.verificationCode)
        verify(mock = userRepository, mode = never()).save(any<User>())
        verify(mock = userTokenRepository, mode = never())
            .deleteUserTokensByUserIdAndType(id = any<UUID>(), type = any<TokenType>())
    }
}