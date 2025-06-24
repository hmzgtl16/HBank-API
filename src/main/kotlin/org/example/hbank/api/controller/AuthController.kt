package org.example.hbank.api.controller

import jakarta.validation.Valid
import org.example.hbank.api.request.LoginRequest
import org.example.hbank.api.response.AuthResponse
import org.example.hbank.api.service.JwtService
import org.example.hbank.api.util.UnauthorizedException
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/users")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService
) {

    @PostMapping("login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> =
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            )

            val userDetails = userDetailsService.loadUserByUsername(loginRequest.username)
            val response = jwtService
                .generateAuthTokens(userDetails.username, userDetails.authorities)

            ResponseEntity.ok(response)
        } catch (_: BadCredentialsException) {
            throw UnauthorizedException()
        }

    @PostMapping("refresh")
    fun refreshTokens(@RequestHeader("x-refresh-token") token: String): ResponseEntity<AuthResponse> =
        try {
            val username = jwtService.decodeToken(token = token)

            val userDetails = userDetailsService.loadUserByUsername(username)

            val response = jwtService
                .generateAuthTokens(username = userDetails.username, grantedAuthorities = userDetails.authorities)

            ResponseEntity.ok(response)
        } catch (_: JwtException) {
            throw UnauthorizedException()
        }
}