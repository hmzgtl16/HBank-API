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
import java.time.Instant
import java.util.stream.Collectors

@Service
class JwtService(
    private val clock: Clock,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder
) {

    fun generateAuthTokens(
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

    private fun generateAccessToken(
        username: String,
        authorities: Array<String>
    ): String = generateToken(username = username, authorities = authorities, expiration = Duration.ofHours(1))

    private fun generateRefreshToken(
        username: String,
        authorities: Array<String>
    ): String = generateToken(username = username, authorities = authorities, expiration = Duration.ofDays(7))

 /*   @Throws(JWTCreationException::class)
    fun generateAccessToken(
        username: String,
        authorities: Array<String>
    ): String = JWT.create()
        .withSubject(username)
        .withIssuer(ISSUER)
        .withExpiresAt(Date(Instant.now(clock).toEpochMilli() + jwtProperties.expiresAt))
        .withClaim(IS_REFRESH_CLAIM, false)
        .withArrayClaim(AUTHORITIES_CLAIM, authorities)
        .sign(Algorithm.HMAC512(jwtProperties.secretKey.toByteArray()))

    @Throws(JWTCreationException::class)
    fun generateRefreshToken(
        username: String,
        authorities: Array<String>
    ): String = JWT.create()
        .withSubject(username)
        .withIssuer(ISSUER)
        .withClaim(IS_REFRESH_CLAIM, true)
        .withArrayClaim(AUTHORITIES_CLAIM, authorities)
        .sign(Algorithm.HMAC512(jwtProperties.secretKey.toByteArray()))

    @Throws(
        JWTVerificationException::class,
        JWTDecodeException::class,
        TokenExpiredException::class,
        AlgorithmMismatchException::class
    )
    fun verifyToken(token: String): DecodedJWT = JWT
        .require(Algorithm.HMAC512(jwtProperties.secretKey.toByteArray()))
        .withIssuer(ISSUER)
        .build()
        .verify(token)

    fun isAccessToken(
        decodedJWT: DecodedJWT
    ): Boolean = decodedJWT.getClaim(IS_REFRESH_CLAIM).let {
        it.isMissing.not() && it.asBoolean().not()
    }

    fun isRefreshToken(decodedJWT: DecodedJWT): Boolean =
        decodedJWT.getClaim(IS_REFRESH_CLAIM).let {
            it.isMissing.not() && it.asBoolean()
        }

    fun isValidBearerToken(bearerToken: String): Boolean =
        bearerToken.isNotEmpty() && bearerToken.startsWith("Bearer ")

    fun extractToken(bearerToken: String): String =
        bearerToken.substring(startIndex = 7)


    fun extractSubject(
        bearerToken: String,
        isAccess: Boolean = false,
        isRefresh: Boolean = false
    ): String {

        val token = if (isRefresh) bearerToken else extractToken(bearerToken = bearerToken)
        val decodedJWT = verifyToken(token = token)
        val isAccessToken = isAccessToken(decodedJWT = decodedJWT)
        val isRefreshToken = isRefreshToken(decodedJWT = decodedJWT)

        return when {
            isAccess && isAccessToken -> decodedJWT.subject
            isRefresh && isRefreshToken -> decodedJWT.subject
            else -> ""
        }
    }*/

    fun decodeRefreshToken(token: String): String =
        jwtDecoder.decode(token).subject

    private fun generateToken(
        username: String,
        authorities: Array<String>,
        expiration: Duration,
    ): String {
        val now = Instant.now(clock)

        val claims = JwtClaimsSet.builder()
            .issuer("HBank")
            .issuedAt(now)
            .expiresAt(expiration.addTo(now) as Instant)
            .subject(username)
            .claim("scope", authorities)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    /*companion object {
        private const val ISSUER = "WaseetPay-API"
        private const val AUTHORITIES_CLAIM = "authorities"
        private const val IS_REFRESH_CLAIM = "is_refresh"
    }*/
}

