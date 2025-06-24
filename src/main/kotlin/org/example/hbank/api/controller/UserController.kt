package org.example.hbank.api.controller

import jakarta.validation.Valid
import org.example.hbank.api.request.*
import org.example.hbank.api.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("register")
    fun registerUser(@Valid @RequestBody registerUserRequest: RegisterUserRequest): ResponseEntity<Nothing> {
        userService.registerUser(request = registerUserRequest)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("verify/email/send")
    fun sendVerifyEmail(@Valid @RequestBody sendVerifyEmailRequest: SendVerifyEmailRequest): ResponseEntity<Nothing> {
        userService.sendVerifyEmail(request = sendVerifyEmailRequest)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("verify/email")
    fun verifyEmail(@Valid @RequestBody verifyEmailRequest: VerifyEmailRequest): ResponseEntity<Nothing> {
        userService.verifyEmail(request = verifyEmailRequest)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("password/forgot")
    fun forgotPassword(@Valid @RequestBody forgotPasswordRequest: ForgotPasswordRequest): ResponseEntity<Nothing> {
        userService.forgotPassword(request = forgotPasswordRequest)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("password/reset")
    fun resetPassword(@Valid @RequestBody resetPasswordRequest: ResetPasswordRequest): ResponseEntity<Nothing> {
        userService.resetPassword(request = resetPasswordRequest)
        return ResponseEntity.noContent().build()
    }
}
