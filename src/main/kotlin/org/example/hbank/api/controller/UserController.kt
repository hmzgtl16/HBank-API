package org.example.hbank.api.controller

import org.example.hbank.api.response.AuthResponse
import org.example.hbank.api.service.*
import org.example.hbank.api.utility.Errors
import org.example.hbank.api.utility.Validator
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping(path = ["api/v1/user"])
class UserController(
    transactionManager: PlatformTransactionManager,
    private val jwtService: JwtService,
    private val userService: UserService,
    private val customerService: CustomerService,
    private val roleService: RoleService,
    private val tokenService: TokenService,
    private val mailService: MailService
) {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    @PostMapping(
        path = ["login"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun login(
        authentication: Authentication
    ): ResponseEntity<AuthResponse> {
        try {
            // Generate JWT tokens
            val response = jwtService.generateAuthTokens(
                username = authentication.name,
                grantedAuthorities = authentication.authorities
            )

            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            when (e) {
                is ResponseStatusException -> throw e
                else -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED, Errors.USER_NOT_FOUND)
            }
        }
    }

    @PostMapping(
        path = ["register"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun registerUser(
        @RequestParam(name = "email")
        email: String,
        @RequestParam(name = "username")
        username: String,
        @RequestParam(name = "password")
        password: String,
        @RequestParam(name = "confirm_password")
        confirmPassword: String
    ): ResponseEntity<Nothing> {

        if (!Validator.isValidEmail(email = email))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMAIL_INVALID)

        if (!Validator.isValidUsername(username = username))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.USERNAME_INVALID)

        if (!Validator.isValidPassword(password = password))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.PASSWORD_INVALID)

        if (!Validator.isPasswordMatches(password1 = password, password2 = confirmPassword))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.PASSWORD_NOT_MATCHES)

        if (userService.userExistsByEmail(email = email))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMAIL_ALREADY_EXISTS)

        if (userService.userExistsByUsername(username = username))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.USERNAME_ALREADY_EXISTS)

        transactionTemplate.execute {
            val user = userService.registerUser(
                email = email,
                username = username,
                password = password
            )

            val role = roleService.getRoleUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.ROLE_NOT_FOUND)

            userService.addRoleToUser(role = role, user = user)

            val token = tokenService.createToken()

            tokenService.createVerifyEmailToken(user = user, token = token)
        }?.let {
            mailService.sendVerifyEmailEmail(
                username = it.user.username,
                email = it.user.email,
                token = it.token.value
            )
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        path = ["verify/email/send"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun sendVerificationEmail(@RequestParam(name = "email") email: String): ResponseEntity<Nothing> {

        if (!Validator.isValidEmail(email = email))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMAIL_INVALID)

        if (!userService.userExistsByEmail(email = email))
            throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.EMAIL_NOT_FOUND)

        if (userService.isUserVerified(email = email))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMAIL_ALREADY_VERIFIED)

        transactionTemplate.execute {
            val user = userService.getUserByEmail(email = email)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.EMAIL_NOT_FOUND)

            val token = tokenService.createToken()

            tokenService.createVerifyEmailToken(user = user, token = token)
        }?.let {
            mailService.sendVerifyEmailEmail(
                username = it.user.username,
                email = it.user.email,
                token = it.token.value
            )
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        path = ["verify/email"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun verifyEmail(@RequestParam(name = "token") token: String): ResponseEntity<Nothing> {

        if (!Validator.isValidToken(token = token))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TOKEN_INVALID)

        transactionTemplate.executeWithoutResult {

            val verifyEmailToken = tokenService.getVerifyEmailToken(value = token)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TOKEN_NOT_FOUND)

            if (tokenService.isVerifyEmailTokenExpired(created = verifyEmailToken.token.created))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TOKEN_EXPIRED)

            val user = userService.getUserByVerifyEmailToken(verifyEmailToken = verifyEmailToken)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TOKEN_NOT_FOUND)

            userService.enableUser(user = user)
            tokenService.deleteVerifyEmailTokens(user = user)
            customerService.createConsumer(user = user)
        }

        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        path = ["password/forgot"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun forgotPassword(
        @RequestParam(name = "email")
        email: String
    ): ResponseEntity<Nothing> {

        if (!Validator.isValidEmail(email = email))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMAIL_INVALID)

        transactionTemplate.execute {

            val user = userService.getUserByEmail(email = email)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.EMAIL_NOT_FOUND)

            if (!user.enabled)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMAIL_NOT_VERIFIED)

            tokenService.deleteResetPasswordTokens(user = user)
            val token = tokenService.createToken()

            tokenService.createResetPasswordToken(user = user, token = token)
        }?.let {
            mailService.sendForgotPasswordEmail(
                username = it.user!!.username,
                email = it.user!!.email,
                token = it.token!!.value
            )
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        path = ["password/reset"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun resetPassword(
        @RequestParam(name = "token") token: String,
        @RequestParam(name = "password") password: String,
        @RequestParam(name = "confirm_password") confirmPassword: String
    ): ResponseEntity<Nothing> {

        if (!Validator.isValidToken(token = token))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TOKEN_INVALID)

        if (!Validator.isValidPassword(password = password))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.PASSWORD_INVALID)

        if (!Validator.isPasswordMatches(password1 = password, password2 = confirmPassword))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.PASSWORD_NOT_MATCHES)

        transactionTemplate.executeWithoutResult {
            val resetPasswordToken = tokenService.getResetPasswordToken(value = token)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TOKEN_NOT_FOUND)

            if (tokenService.isResetPasswordTokenExpired(created = resetPasswordToken.token!!.created))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TOKEN_EXPIRED)

            tokenService.deleteResetPasswordTokens(user = resetPasswordToken.user!!)
            userService.resetPassword(
                user = resetPasswordToken.user!!,
                password = password
            )
        }

        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        path = ["refresh"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun refreshTokens(
        @RequestParam(name = "x-refresh-token")
        token: String
    ): ResponseEntity<AuthResponse> = try {
        val subject = jwtService.decodeRefreshToken(token = token)
        val userDetails = userService.loadUserByUsername(username = subject)
        val response = jwtService.generateAuthTokens(
            username = userDetails.username,
            grantedAuthorities = userDetails.authorities
        )
        ResponseEntity.ok(response)
    } catch (_: Exception) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST)
    }
}
