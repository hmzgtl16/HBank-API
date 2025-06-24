package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.example.hbank.api.model.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import kotlin.test.Test

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestContainersConfig::class)
@Sql("/database/schema.sql")
class RoleRepositoryTest {

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Test
    fun `should get role by name`() {


        val foundRole = roleRepository.findRoleByName("ROLE::USER")

        assertThat(foundRole).isNotNull
        assertThat(foundRole!!).matches {
            it.name == "ROLE::USER" &&
                    it.id == UUID.fromString("2828b25e-5e9a-4c38-f026-6c9bbd22230d")
        }
    }
}