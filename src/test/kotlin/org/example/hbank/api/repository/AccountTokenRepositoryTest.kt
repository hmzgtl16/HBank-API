package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.example.hbank.api.model.AccountTokenId
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
class AccountTokenRepositoryTest {

    @Autowired
    private lateinit var accountTokenRepository: AccountTokenRepository

    @Test
    fun `should find account token by value`() {
        val foundAccountToken = accountTokenRepository.findAccountTokenByTokenValue("928892")

        assertThat(foundAccountToken).isNotNull
        assertThat(foundAccountToken!!).matches {
            it.id == AccountTokenId(
                tokenId = UUID.fromString("cf78f5a6-860b-46bf-bd64-c3854e52c452"),
                accountId = UUID.fromString("1b3d1d84-a234-4418-9cb5-82e9d721976c")
            )
        }
    }

    @Test
    fun `should check if account exists by number`() {
        assertThat(
            accountTokenRepository.existsAccountTokenByAccountId(UUID.fromString("1b3d1d84-a234-4418-9cb5-82e9d721976c"))
        ).isTrue
        assertThat(
            accountTokenRepository.existsAccountTokenByAccountId(UUID.fromString("cf78f5a6-860b-46bf-bd64-c3854e52c452"))
        ).isFalse
    }
}
