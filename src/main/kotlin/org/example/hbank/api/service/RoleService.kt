package org.example.hbank.api.service

import org.example.hbank.api.repository.RoleRepository
import org.example.hbank.api.utility.Roles
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {

    fun getRoleUser() = roleRepository.getRoleByName(name = Roles.ROLE_USER)

}
