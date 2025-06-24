package org.example.hbank.api.repository

import org.example.hbank.api.model.AccountToken
import org.example.hbank.api.model.AccountTokenId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountTokenRepository : JpaRepository<AccountToken, AccountTokenId> {

    fun findAccountTokenByTokenValue(value: String): AccountToken?

    fun existsAccountTokenByAccountId(id: UUID): Boolean
}