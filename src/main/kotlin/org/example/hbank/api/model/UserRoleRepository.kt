package org.example.hbank.api.model

import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<UserRole, UserRoleId> {
}