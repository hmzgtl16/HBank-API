package org.example.hbank.api.service

import org.example.hbank.api.response.AuthResponse
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.util.stream.Collectors

interface JwtService {
    fun generateAuthTokens(username: String, grantedAuthorities: Collection<GrantedAuthority>): AuthResponse
    fun decodeToken(token: String): String
}



@Service
class JwtServiceImpl(
    private val clock: Clock,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder
): JwtService {

    override fun generateAuthTokens(
        username: String,
        grantedAuthorities: Collection<GrantedAuthority>
    ): AuthResponse {

        val authorities = grantedAuthorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList())
            .toTypedArray()

        val accessToken = generateAccessToken(
            username = username,
            authorities = authorities
        )

        val refreshToken = generateRefreshToken(
            username = username,
            authorities = authorities
        )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    override fun decodeToken(token: String): String =
        jwtDecoder.decode(token).subject

    private fun generateAccessToken(
        username: String,
        authorities: Array<String>
    ): String = generateToken(username = username, authorities = authorities, expiration = Duration.ofHours(1))

    private fun generateRefreshToken(
        username: String,
        authorities: Array<String>
    ): String = generateToken(username = username, authorities = authorities, expiration = Duration.ofDays(7))

    private fun generateToken(
        username: String,
        authorities: Array<String>,
        expiration: Duration,
    ): String {
        val now = clock.instant()

        val claims = JwtClaimsSet.builder()
            .issuer("HBank")
            .subject(username)
            .issuedAt(now)
            .expiresAt(now.plus(expiration))
            .claim("scope", authorities)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

}

