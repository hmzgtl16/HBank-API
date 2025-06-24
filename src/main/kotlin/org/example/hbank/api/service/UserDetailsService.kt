package org.example.hbank.api.service

import org.example.hbank.api.model.Privilege
import org.example.hbank.api.model.Role
import org.example.hbank.api.model.UserRole
import org.example.hbank.api.repository.UserRepository
import org.example.hbank.api.util.Errors
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException


@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {

        val user = userRepository.findUserByUsername(username = username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

        val authorities = user.roles
            .map(UserRole::role)
            .flatMap(Role::privileges)
            .map(Privilege::name)
            .map(::SimpleGrantedAuthority)

        return User.builder()
            .username(user.username)
            .password(user.password)
            .disabled(!user.enabled)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .build()
    }
}