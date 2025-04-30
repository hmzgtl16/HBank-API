package org.example.hbank.api.repository

import org.example.hbank.api.model.Account
import org.example.hbank.api.model.AccountToken
import org.example.hbank.api.model.AccountTokenId
import org.springframework.data.jpa.repository.JpaRepository

interface AccountTokenRepository : JpaRepository<AccountToken, AccountTokenId> {

    fun findAccountTokenByTokenValue(token: String): AccountToken?
    fun findAccountTokenByAccount(account: Account): AccountToken?
}