package org.example.hbank.api.repository

import org.example.hbank.api.model.User
import org.example.hbank.api.model.UserVerifyEmailToken
import org.example.hbank.api.model.UserVerifyEmailTokenId
import org.springframework.data.jpa.repository.JpaRepository

interface UserVerifyEmailTokenRepository : JpaRepository<UserVerifyEmailToken, UserVerifyEmailTokenId> {

    fun findUserVerifyEmailTokenByTokenValue(value: String) : UserVerifyEmailToken?

    fun deleteUserVerifyEmailTokensByUser(user: User)
}