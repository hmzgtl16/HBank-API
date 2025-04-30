package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Privilege
import org.example.hbank.api.model.Role
import org.example.hbank.api.model.RolePrivilege
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:postgresql://localhost:5432/hbank_db",
    "spring.datasource.username=hbank_root",
    "spring.datasource.password=hbank_pass",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
])
class RolePrivilegeRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var rolePrivilegeRepository: RolePrivilegeRepository

    @Test
    fun `should find role privileges by role`() {
        // Arrange
        val privilege = createPrivilege("TEST_PRIVILEGE")
        entityManager.persist(privilege)

        val role = createRole("TEST_ROLE")
        entityManager.persist(role)

        val rolePrivilege = createRolePrivilege(role, privilege)
        entityManager.persist(rolePrivilege)
        entityManager.flush()

        // Act
        val found = rolePrivilegeRepository.findRolePrivilegesByRole(role)

        // Assert
        assertThat(found).isNotEmpty
        assertThat(found).contains(rolePrivilege)
        assertThat(found.first { it == rolePrivilege }.role).isEqualTo(role)
        assertThat(found.first { it == rolePrivilege }.privilege).isEqualTo(privilege)
    }

    @Test
    fun `should return empty list when no role privileges exist for role`() {
        val role = createRole("TEST_ROLE")
        entityManager.persist(role)
        entityManager.flush()

         // Act
        val found = rolePrivilegeRepository.findRolePrivilegesByRole(role)

        // Assert
        assertThat(found).isEmpty()
    }

    private fun createPrivilege(name: String): Privilege =
        Privilege(name = name)

    private fun createRole(name: String): Role =
        Role(name = name)

    private fun createRolePrivilege(role: Role, privilege: Privilege): RolePrivilege =
        RolePrivilege(role = role, privilege = privilege)
}