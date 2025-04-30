package org.example.hbank.api.repository

import org.example.hbank.api.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RoleRepository : JpaRepository<Role, UUID> {

    fun getRoleByName(name: String): Role?
}