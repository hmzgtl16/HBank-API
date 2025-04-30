package org.example.hbank.api.service

import org.example.hbank.api.utility.Errors
import jakarta.transaction.Transactional
import org.example.hbank.api.model.*
import org.example.hbank.api.repository.RolePrivilegeRepository
import org.example.hbank.api.repository.UserRepository
import org.example.hbank.api.repository.UserRoleRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.time.Instant

@Service
@Transactional
class UserService(
    transactionManager: PlatformTransactionManager,
    private val clock: Clock,
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val rolePrivilegeRepository: RolePrivilegeRepository
) : UserDetailsService {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    fun registerUser(
        email: String,
        username: String,
        password: String,
    ): User {

        val user = User(
            email = email,
            username = username,
            password = BCryptPasswordEncoder().encode(password),
            created = Instant.now(clock)
        )

        return userRepository.save(user)
    }

    fun enableUser(user: User): User {
        user.enabled = true

        return userRepository.save(user)
    }

    fun resetPassword(user: User, password: String): User {
        user.password = BCryptPasswordEncoder().encode(password)
        return userRepository.save(user)
    }

    fun getUserByUsername(username: String): User? =
        userRepository.findUserByUsername(username = username)

    fun getUserByEmail(email: String): User? =
        userRepository.findUserByEmail(email = email)

    fun getUserByPhoneNumber(phoneNumber: String): User? =
        userRepository.findUserByPhoneNumber(phoneNumber = phoneNumber)

    fun getUserByVerifyEmailToken(verifyEmailToken: UserVerifyEmailToken): User? = verifyEmailToken.user

    fun getUserByResetPasswordToken(resetPasswordToken: UserResetPasswordToken): User? = resetPasswordToken.user

    fun userExistsByUsername(username: String): Boolean =
        userRepository.existsUserByUsername(username = username)

    fun userExistsByEmail(email: String): Boolean =
        userRepository.existsUserByEmail(email = email)

    fun userExistsByPhoneNumber(phoneNumber: String): Boolean =
        userRepository.existsUserByPhoneNumber(phoneNumber = phoneNumber)

    fun getAuthenticatedUser(): User? {
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails

        return getUserByUsername(username = userDetails.username)
    }

    fun getUserPrivileges(user: User): List<Privilege> =
        userRoleRepository
            .findUserRolesByUser(user = user)
            .mapNotNull(UserRole::role)
            .flatMap(rolePrivilegeRepository::findRolePrivilegesByRole)
            .mapNotNull(RolePrivilege::privilege)

    override fun loadUserByUsername(username: String): UserDetails = transactionTemplate
        .execute {
            val user = getUserByUsername(username = username)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.USER_NOT_FOUND)

            val authorities = getUserPrivileges(user = user)
                .map(Privilege::asAuthority)

            org.springframework.security.core.userdetails.User(
                user.username,
                user.password,
                user.enabled,
                true,
                true,
                true,
                authorities
            )
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.USER_NOT_FOUND)

    fun isUserVerified(email: String): Boolean =
        userRepository.existsUserByEmailAndEnabledIsTrue(email = email)

    fun addRoleToUser(role: Role, user: User): UserRole {
        val userRole = UserRole(user = user, role = role)

        return userRoleRepository.save(userRole)
    }
}

private fun Privilege.asAuthority(): GrantedAuthority = SimpleGrantedAuthority(name)
