package org.example.hbank.api.repository

import org.example.hbank.api.model.User
import org.example.hbank.api.model.UserResetPasswordToken
import org.example.hbank.api.model.UserResetPasswordTokenId
import org.springframework.data.jpa.repository.JpaRepository

interface UserResetPasswordTokenRepository : JpaRepository<UserResetPasswordToken, UserResetPasswordTokenId> {

    fun findUserResetPasswordTokenByTokenValue(value: String): UserResetPasswordToken?

    fun deleteUserResetPasswordTokensByUser(user: User)
}