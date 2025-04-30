package org.example.hbank.api.repository

import org.example.hbank.api.model.User
import org.example.hbank.api.model.UserRole
import org.example.hbank.api.model.UserRoleId
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<UserRole, UserRoleId> {

    fun findUserRolesByUser(user: User): List<UserRole>
}