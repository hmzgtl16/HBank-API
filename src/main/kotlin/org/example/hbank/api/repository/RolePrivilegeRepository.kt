package org.example.hbank.api.repository

import org.example.hbank.api.model.Role
import org.example.hbank.api.model.RolePrivilege
import org.example.hbank.api.model.RolePrivilegeId
import org.springframework.data.jpa.repository.JpaRepository

interface RolePrivilegeRepository : JpaRepository<RolePrivilege, RolePrivilegeId> {

    fun findRolePrivilegesByRole(role: Role): List<RolePrivilege>
}