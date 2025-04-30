package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Token
import org.example.hbank.api.model.User
import org.example.hbank.api.model.UserResetPasswordToken
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
class UserResetPasswordTokenRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @Autowired
    private lateinit var userResetPasswordTokenRepository: UserResetPasswordTokenRepository

    private lateinit var user: User

    private lateinit var token1: Token
    private lateinit var token2: Token
    private lateinit var token3: Token

    private lateinit var userResetPasswordToken1: UserResetPasswordToken
    private lateinit var userResetPasswordToken2: UserResetPasswordToken
    private lateinit var userResetPasswordToken3: UserResetPasswordToken


    @BeforeEach
    fun setup() {

        user = createUser()
        entityManager.persist(user)

        token1 = createToken("test-token-value")
        entityManager.persist(token1)

        token2 = createToken("test-token-value2")
        entityManager.persist(token2)

        token3 = createToken("test-token-value3")
        entityManager.persist(token3)

        userResetPasswordToken1 = createUserResetPasswordToken(user, token1)
        entityManager.persist(userResetPasswordToken1)

        userResetPasswordToken2 = createUserResetPasswordToken(user, token2)
        entityManager.persist(userResetPasswordToken2)

        userResetPasswordToken3 = createUserResetPasswordToken(user, token3)
        entityManager.persist(userResetPasswordToken3)

        entityManager.flush()
    }

    @Test
    fun `should find user reset password token by token value`() {

        val found =
            userResetPasswordTokenRepository.findUserResetPasswordTokenByTokenValue( "test-token-value")

        assertThat(found).isNotNull
        assertThat(found?.token?.value).isEqualTo("test-token-value")
        assertThat(found?.user?.id).isEqualTo(user.id)
    }

    @Test
    fun `should return null when user reset password token token value does not exist`() {

        val found =
            userResetPasswordTokenRepository.findUserResetPasswordTokenByTokenValue( "nonexistent-token")

        assertThat(found).isNull()
    }

    @Test
    fun `should delete all user reset password token by user`() {

        userResetPasswordTokenRepository.deleteUserResetPasswordTokensByUser(user)

        assertThat(userResetPasswordTokenRepository.existsById(userResetPasswordToken1.id!!)).isFalse
        assertThat(userResetPasswordTokenRepository.existsById(userResetPasswordToken2.id!!)).isFalse
        assertThat(userResetPasswordTokenRepository.existsById(userResetPasswordToken3.id!!)).isFalse

        assertThat(tokenRepository.existsById(token1.id!!)).isFalse
        assertThat(tokenRepository.existsById(token2.id!!)).isFalse
        assertThat(tokenRepository.existsById(token3.id!!)).isFalse
    }

    private fun createUser(): User {
        return User(
            username = "johndoe",
            email = "john.doe@example.com",
            password = "password123",
            created = Instant.now()
        )
    }

    private fun createToken(value: String): Token =
        Token(value = value, created = Instant.now())

    private fun createUserResetPasswordToken(user: User, token: Token): UserResetPasswordToken =
        UserResetPasswordToken(user = user, token = token)
}