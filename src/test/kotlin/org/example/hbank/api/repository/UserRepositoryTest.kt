package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.User
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
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User
    private lateinit var user4: User
    private lateinit var user5: User
    private lateinit var user6: User
    private lateinit var user7: User
    private lateinit var user8: User
    private lateinit var user9: User
    private lateinit var user10: User

    @BeforeEach
    fun setUp() {

        user1 = createUser(
            email = "jane.doe@example.com",
            username = "janedoe",
            password = "securePass",
            phoneNumber = "0661 23 45 67"
        )
        entityManager.persist(user1)

        user2 = createUser(
            email = "peter.pan@neverland.com",
            username = "p_pan",
            password = "flyAway1",
            phoneNumber = "0655 87 65 43"
        )
        user2.enabled = true
        entityManager.persist(user2)

        user3 = createUser(
            email = "alice.wonder@underland.net",
            username = "curiousAlice",
            password = "eatMeDrinkMe",
            phoneNumber = "0678 90 12 34"
        )
        entityManager.persist(user3)

        user4 = createUser(
            email = "bob.builder@construct.org",
            username = "can_fix_it",
            password = "yesWeCan",
            phoneNumber = "0699 54 32 10"
        )
        entityManager.persist(user4)

        user5 = createUser(
            email = "charlie.brown@peanuts.com",
            username = "goodOlCharlie",
            password = "woofWoof",
            phoneNumber = "0666 11 22 33"
        )
        user5.enabled = true
        entityManager.persist(user5)

        user6 = createUser(
            email = "lucy.vanpelt@peanuts.com",
            username = "psychiatrist",
            password = "nickelPlease",
            phoneNumber = "0651 47 85 23"
        )
        entityManager.persist(user6)

        user7 = createUser(
            email = "snoopy@peanuts.com",
            username = "flyingAce",
            password = "redBaronDown",
            phoneNumber = "0672 58 96 32"
        )
        entityManager.persist(user7)

        user8 = createUser(
            email = "bart.simpson@springfield.tv",
            username = "el_barto",
            password = "eatMyShorts",
            phoneNumber = "0693 15 79 48"
        )
        user8.enabled = true
        entityManager.persist(user8)

        user9 = createUser(
            email = "lisa.simpson@springfield.tv",
            username = "smart_lisa",
            password = "jazzLover",
            phoneNumber = "0664 82 16 95"
        )
        entityManager.persist(user9)

        user10 = createUser(
            email = "homer.simpson@springfield.tv",
            username = "donutKing",
            password = "dohNut",
            phoneNumber = "0658 39 71 42"
        )
        entityManager.persist(user10)

        entityManager.flush()
    }

    @Test
    fun `should find user by email`() {

        val found = userRepository.findUserByEmail("snoopy@peanuts.com")

        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(user7.id)
    }

    @Test
    fun `should return null when no user email does not exist`() {

        val found = userRepository.findUserByEmail("")

        assertThat(found).isNull()
    }

    @Test
    fun `should find user by username`() {

        val found = userRepository.findUserByUsername("donutKing")

        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(user10.id)
    }

    @Test
    fun `should return null when no user username does not exist`() {

        val found = userRepository.findUserByUsername("")

        assertThat(found).isNull()
    }

    @Test
    fun `should find user by phone number`() {

        val found = userRepository.findUserByPhoneNumber("0651 47 85 23")

        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(user6.id)
    }

    @Test
    fun `should return null when no user phone number does not exist`() {

        val found = userRepository.findUserByPhoneNumber("")

        assertThat(found).isNull()
    }

    @Test
    fun `should check if user exists by email`() {

        assertThat(userRepository.existsUserByEmail("lisa.simpson@springfield.tv")).isTrue
        assertThat(userRepository.existsUserByEmail("alice.vanpelt@underland.net")).isFalse
    }

    @Test
    fun `should check if user exists by username`() {

        assertThat(userRepository.existsUserByUsername("can_fix_it")).isTrue
        assertThat(userRepository.existsUserByUsername("eatMeDrinkMe")).isFalse
    }

    @Test
    fun `should check if user exists by phone number`() {

        assertThat(userRepository.existsUserByPhoneNumber("0651 47 85 23")).isTrue
        assertThat(userRepository.existsUserByPhoneNumber("0669 54 32 10")).isFalse
    }

    @Test
    fun `should check if user exists by email and is enabled`() {

        assertThat(userRepository.existsUserByEmailAndEnabledIsTrue("peter.pan@neverland.com")).isTrue
        assertThat(userRepository.existsUserByEmailAndEnabledIsTrue("lucy.vanpelt@peanuts.com")).isFalse
    }

    private fun createUser(
        email: String,
        username: String,
        password: String,
        phoneNumber: String
    ): User =
        User(
            email = email,
            username = username,
            password = password,
            phoneNumber = phoneNumber,
            created = Instant.now()
        )
}