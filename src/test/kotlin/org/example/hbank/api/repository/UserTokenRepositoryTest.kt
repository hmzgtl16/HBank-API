package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.example.hbank.api.model.UserTokenId
import org.example.hbank.api.util.TokenType
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
class UserTokenRepositoryTest {

    @Autowired
    private lateinit var userTokenRepository: UserTokenRepository

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @Test
    fun `should find user token by value`() {
        val foundUserToken = userTokenRepository.findUserTokenByTokenValue("926392")

        assertThat(foundUserToken).isNotNull
        assertThat(foundUserToken!!).matches {
            it.id == UserTokenId(
                tokenId = UUID.fromString("cf78f5a6-860b-46bf-bd14-c3854e52c452"),
                userId = UUID.fromString("e167335c-4a2e-438d-9349-cb4bc4570268")
            )
        }
    }

    @Test
    fun `delete user tokens by user and type`() {
        userTokenRepository.deleteUserTokensByUserIdAndType(
            id = UUID.fromString("e167335c-4a2e-438d-9349-cb4bc4570268"),
            type = TokenType.VERIFY_EMAIL
        )

        val remainingUserTokens = userTokenRepository.findAll()
            .filter {
                it.id.userId == UUID.fromString("e167335c-4a2e-438d-9349-cb4bc4570268") &&
                        it.type == TokenType.VERIFY_EMAIL
            }

        assertThat(tokenRepository.existsTokenByValue("657447")).isFalse
        assertThat(tokenRepository.existsTokenByValue("926392")).isFalse
        assertThat(remainingUserTokens).isEmpty()
    }
}