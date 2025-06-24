package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestContainersConfig::class)
@Sql("/database/schema.sql")
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should find user by email`() {
        val foundUser = userRepository.findUserByEmail("dani-lim@test.org")

        assertThat(foundUser).isNotNull
        assertThat(foundUser!!).matches {
            it.id == UUID.fromString("e167335c-4a2e-438d-9349-cb4bc4570268") &&
                    it.username == "dani-lim" &&
                    it.email == "dani-lim@test.org" &&
                    it.phoneNumber == "+1987654321"
        }
    }

    @Test
    fun `should find user by username`() {

        val foundUser = userRepository.findUserByUsername("dani-lim")

        assertThat(foundUser).isNotNull
        assertThat(foundUser!!).matches {
            it.id == UUID.fromString("e167335c-4a2e-438d-9349-cb4bc4570268") &&
                    it.username == "dani-lim" &&
                    it.email == "dani-lim@test.org" &&
                    it.phoneNumber == "+1987654321"
        }
    }

    @Test
    fun `should find user by phone number`() {

        val foundUser = userRepository.findUserByPhoneNumber("+1987654321")

        assertThat(foundUser).isNotNull
        assertThat(foundUser!!).matches {
            it.id == UUID.fromString("e167335c-4a2e-438d-9349-cb4bc4570268") &&
                    it.username == "dani-lim" &&
                    it.email == "dani-lim@test.org" &&
                    it.phoneNumber == "+1987654321"
        }
    }

    @Test
    fun `should check if user exists by email`() {
        assertThat(userRepository.existsUserByEmail("david.williams@company.net")).isTrue
        assertThat(userRepository.existsUserByEmail("alice.vanpelt@underland.net")).isFalse
    }

    @Test
    fun `should check if user exists by username`() {

        assertThat(userRepository.existsUserByUsername("alice_johnson_a7c3")).isTrue
        assertThat(userRepository.existsUserByUsername("eve.williams_be18")).isFalse
    }

    @Test
    fun `should check if user exists by phone number`() {

        assertThat(userRepository.existsUserByPhoneNumber("+1987654321")).isTrue
        assertThat(userRepository.existsUserByPhoneNumber("+1846755421")).isFalse
    }

    @Test
    fun `should check if user exists by email and is enabled`() {

        assertThat(userRepository.existsUserByEmailAndEnabledIsTrue("charlie.jones@test.org")).isTrue
        assertThat(userRepository.existsUserByEmailAndEnabledIsTrue("dani-lim@test.org")).isFalse
    }
}