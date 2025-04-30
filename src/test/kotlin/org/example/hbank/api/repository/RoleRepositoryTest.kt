package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Role
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:postgresql://localhost:5432/hbank_db",
    "spring.datasource.username=hbank_root",
    "spring.datasource.password=hbank_pass",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
])
class RoleRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Test
    fun `should get role by name`() {
        // Arrange
        val role = createRole("TEST_ROLE")
        entityManager.persist(role)
        entityManager.flush()

        // Act
        val found = roleRepository.getRoleByName(role.name!!)

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(role.id)
        assertThat(found?.name).isEqualTo("TEST_ROLE")
    }

    @Test
    fun `should return null when role name does not exist`() {
          // Act
        val found = roleRepository.getRoleByName("NONEXISTENT_ROLE")

        // Assert
        assertThat(found).isNull()
    }

    private fun createRole(name: String): Role =
        Role(name = name)
}