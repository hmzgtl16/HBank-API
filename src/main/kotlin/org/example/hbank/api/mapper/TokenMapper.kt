package org.example.hbank.api.mapper

import org.example.hbank.api.model.Token
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.temporal.ChronoUnit

@Component
class TokenMapper(private val clock: Clock) {

    fun toEntity(value: String): Token = Token(value = value, createdAt = clock.instant())

    fun isExpired(token: Token): Boolean =
        token.createdAt.plus(10, ChronoUnit.MINUTES).isBefore(clock.instant())
}