package org.example.hbank.api.repository

import org.example.hbank.api.model.UserToken
import org.example.hbank.api.model.UserTokenId
import org.example.hbank.api.util.TokenType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserTokenRepository: JpaRepository<UserToken, UserTokenId> {

    fun findUserTokenByTokenValue(value: String): UserToken?

    fun deleteUserTokensByUserIdAndType(id: UUID, type: TokenType)
}

