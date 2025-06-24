package org.example.hbank.api.repository

import org.example.hbank.api.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    fun findUserByUsername(username: String): User?
    fun findUserByEmail(email: String): User?
    fun findUserByPhoneNumber(phoneNumber: String): User?

    fun existsUserByEmail(email: String): Boolean
    fun existsUserByUsername(username: String): Boolean
    fun existsUserByPhoneNumber(phoneNumber: String): Boolean
    fun existsUserByEmailAndEnabledIsTrue(email: String): Boolean
}

