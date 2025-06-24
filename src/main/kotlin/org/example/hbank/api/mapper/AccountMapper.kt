package org.example.hbank.api.mapper

import kotlinx.datetime.toKotlinInstant
import org.example.hbank.api.model.Account
import org.example.hbank.api.response.PersonalAccountResponse
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class AccountMapper(private val clock: Clock) {

    fun updateEntity(account: Account): Account =
        account.copy(modifiedAt = clock.instant())

    fun toPersonalAccountResponse(account: Account): PersonalAccountResponse = PersonalAccountResponse(
        name = account.name,
        number = account.number,
        balance = account.balance,
        status = account.status,
        limit = account.limit,
        token = account.tokens.first().token.value,
        modifiedAt = account.modifiedAt.toKotlinInstant(),
    )
}