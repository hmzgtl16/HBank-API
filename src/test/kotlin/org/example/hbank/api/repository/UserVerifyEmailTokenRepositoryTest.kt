package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Token
import org.example.hbank.api.model.User
import org.example.hbank.api.model.UserVerifyEmailToken
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
class UserVerifyEmailTokenRepositoryTest {
    
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @Autowired
    private lateinit var userVerifyEmailTokenRepository: UserVerifyEmailTokenRepository
    
    private lateinit var user: User

    private lateinit var token1: Token
    private lateinit var token2: Token
    private lateinit var token3: Token
    
    private lateinit var userVerifyEmailToken1: UserVerifyEmailToken
    private lateinit var userVerifyEmailToken2: UserVerifyEmailToken
    private lateinit var userVerifyEmailToken3: UserVerifyEmailToken
    
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

        userVerifyEmailToken1 = createUserVerifyEmailToken(user, token1)
        entityManager.persist(userVerifyEmailToken1)

        userVerifyEmailToken2 = createUserVerifyEmailToken(user, token2)
        entityManager.persist(userVerifyEmailToken2)

        userVerifyEmailToken3 = createUserVerifyEmailToken(user, token3)
        entityManager.persist(userVerifyEmailToken3)

        entityManager.flush()
    }
    
    @Test
    fun `should find user verify email token by token value`() {

        val found =
            userVerifyEmailTokenRepository.findUserVerifyEmailTokenByTokenValue( "test-token-value")

        assertThat(found).isNotNull
        assertThat(found?.token?.value).isEqualTo("test-token-value")
        assertThat(found?.user?.id).isEqualTo(user.id)
    }

    @Test
    fun `should return null when user verify email token token value does not exist`() {

        val found =
            userVerifyEmailTokenRepository.findUserVerifyEmailTokenByTokenValue( "nonexistent-token")

        assertThat(found).isNull()
    }

    @Test
    fun `should delete all user verify email token by user`() {

        userVerifyEmailTokenRepository.deleteUserVerifyEmailTokensByUser(user)

        assertThat(userVerifyEmailTokenRepository.existsById(userVerifyEmailToken1.id!!)).isFalse
        assertThat(userVerifyEmailTokenRepository.existsById(userVerifyEmailToken2.id!!)).isFalse
        assertThat(userVerifyEmailTokenRepository.existsById(userVerifyEmailToken3.id!!)).isFalse

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

    private fun createUserVerifyEmailToken(user: User, token: Token): UserVerifyEmailToken =
        UserVerifyEmailToken(user = user, token = token)
}