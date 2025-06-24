package org.example.hbank.api.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.hbank.api.service.JwtService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthorizationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = bearerToken.substring(7)
        val username = jwtService.decodeToken(token = token)

        if (SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)

            val authenticationToken = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            ).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }

            SecurityContextHolder.getContext().authentication = authenticationToken
        }
    }
}