package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Token
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Clock
import java.time.Instant

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:postgresql://localhost:5432/hbank_db",
    "spring.datasource.username=hbank_root",
    "spring.datasource.password=hbank_pass",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
])
class TokenRepositoryTest {

     @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @Test
    fun `should check if token exists by value`() {
        // Arrange
        val token = createToken("test-token-value")
        entityManager.persist(token)
        entityManager.flush()

        // Act & Assert
        assertThat(tokenRepository.existsTokenByValue("test-token-value")).isTrue
        assertThat(tokenRepository.existsTokenByValue("nonexistent")).isFalse
    }

     private fun createToken(value: String): Token =
         Token(value = value, created = Instant.now())

}