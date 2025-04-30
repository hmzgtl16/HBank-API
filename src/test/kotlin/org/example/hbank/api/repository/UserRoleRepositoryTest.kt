package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Role
import org.example.hbank.api.model.User
import org.example.hbank.api.model.UserResetPasswordToken
import org.example.hbank.api.model.UserRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(
    properties = [
        "spring.datasource.url=jdbc:postgresql://localhost:5432/hbank_db",
        "spring.datasource.username=hbank_root",
        "spring.datasource.password=hbank_pass",
        "spring.datasource.driver-class-name=org.postgresql.Driver"
    ]
)
class UserRoleRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRoleRepository: UserRoleRepository

    private lateinit var user1: User
    private lateinit var user2: User

    private lateinit var role1: Role
    private lateinit var role2: Role
    private lateinit var role3: Role

    private lateinit var userRole1: UserRole
    private lateinit var userRole2: UserRole
    private lateinit var userRole3: UserRole

    @BeforeEach
    fun setup() {

       user1 = createUser(
            email = "jane.doe@example.com",
            username = "janedoe",
            password = "securePass"
        )
        entityManager.persist(user1)

        user2 = createUser(
            email = "peter.pan@neverland.com",
            username = "p_pan",
            password = "flyAway1"
        )
        entityManager.persist(user2)

        role1 = createRole("ROLE_USER")
        entityManager.persist(role1)

        role2 = createRole("ROLE_ADMIN")
        entityManager.persist(role2)

        role3 = createRole("ROLE_TEST")
        entityManager.persist(role3)

        userRole1 = createUserRole(user1, role1)
        entityManager.persist(userRole1)

        userRole2 = createUserRole(user1, role2)
        entityManager.persist(userRole2)

        userRole3 = createUserRole(user1, role3)
        entityManager.persist(userRole3)

        entityManager.flush()
    }

    @Test
    fun `should find user roles by user`() {

        val found = userRoleRepository.findUserRolesByUser(user1)

        assertThat(found).isNotEmpty
        assertThat(found).hasSize(3)
    }

    @Test
    fun `should return empty list when no user roles exist for user`() {

        val found = userRoleRepository.findUserRolesByUser(user2)

        assertThat(found).isEmpty()
    }

    private fun createUser(username: String, email: String, password: String): User {
        return User(
            username = username,
            email = email,
            password = password,
            created = Instant.now()
        )
    }

    private fun createRole(name: String): Role =
        Role(name = name)

    private fun createUserRole(user: User, role: Role): UserRole =
        UserRole(user = user, role = role)
}
