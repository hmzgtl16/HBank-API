package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.example.hbank.api.model.TransactionTokenId
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
class TransactionTokenRepositoryTest {

     @Autowired
    private lateinit var transactionTokenRepository: TransactionTokenRepository

    @Test
    fun `should find transaction token by value`() {
        val foundTransactionToken =
            transactionTokenRepository.findTransactionTokensByTokenValue("490654")

        assertThat(foundTransactionToken).isNotNull
        assertThat(foundTransactionToken!!).matches {
            it.id == TransactionTokenId(
                tokenId = UUID.fromString("d654a5c2-6262-40e8-b956-fe059d9cfd99"),
                transactionId = UUID.fromString("f3b5aa09-552c-4ff5-ae5a-9a81167bc368")
            )
        }
    }

    @Test
    fun `delete transaction tokens by user and type`() {
        transactionTokenRepository.deleteTransactionTokensByTransactionId(UUID.fromString("99337103-f591-4e97-a7ce-2a1b30b44459"))

        val remainingUserTokens = transactionTokenRepository.findAll()
            .filter { it.id.transactionId == UUID.fromString("99337103-f591-4e97-a7ce-2a1b30b44459") }

        assertThat(remainingUserTokens).isEmpty()
    }
}
