package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.util.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.util.*

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestContainersConfig::class)
@Sql("/database/schema.sql")
class TransactionRepositoryTest {

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Test
    fun `should find transactions by username and status`() {
        val foundTransactions = transactionRepository
            .findDistinctTransactionsByFromCustomerUserUsernameOrToCustomerUserUsernameAndStatusIn(
                username = "george_brown_5785",
                status = listOf(TransactionStatus.COMPLETED),
                pageable = PageRequest.of(0, 5)
            )

        assertThat(foundTransactions.content).hasSize(4)
        assertThat(foundTransactions.content.map(Transaction::id))
            .containsExactlyInAnyOrder(
                UUID.fromString("18b200c8-2402-4f5b-93ea-1956c4d3b400"),
                UUID.fromString("1daaf98b-2f0e-4b0c-9bd6-80b9cf1b618d"),
                UUID.fromString("5e91b25f-5875-4d8b-b152-c381a8a4e570"),
                UUID.fromString("f63c54f5-654a-4953-82fc-e09543d89163")
            )
    }

    @Test
    fun `should find transaction by reference`() {
        val foundTransaction = transactionRepository
            .findTransactionByReference(UUID.fromString("ad2f345e-5c04-459f-bd9d-2be7b5c38789"))

        assertThat(foundTransaction).isNotNull
        assertThat(foundTransaction!!).matches {
            it.reference == UUID.fromString("ad2f345e-5c04-459f-bd9d-2be7b5c38789") &&
                    it.id == UUID.fromString("b28eac85-17aa-4ec3-9e66-cb350f9fc28e")
        }
    }

    @Test
    fun `should check if transaction exists by reference`() {
        assertThat(transactionRepository.existsTransactionByReference(UUID.fromString("ad2f345e-5c04-459f-bd9d-2be7b5c38789"))).isTrue
        assertThat(transactionRepository.existsTransactionByReference(UUID.fromString("ad2f745e-5c04-479f-bd9d-2be7b7c38779"))).isFalse
    }

    @Test
    fun `should count transactions by from and type and status and modified`() {

        val foundTransactionCount = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = UUID.fromString("2ab07e90-ebdc-4df4-8728-8aeeefcc6031"),
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(foundTransactionCount).isEqualTo(2)
    }

    @Test
    fun `should count transactions by to and type and status and modified`() {

        val foundTransactionCount = transactionRepository.countDistinctTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = UUID.fromString("1b3d1d84-a234-4418-9cb5-82e9d721976c"),
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(foundTransactionCount).isEqualTo(2)
    }

    @Test
    fun `should calculate amount sum of transactions by from and type and status and modified`() {
        val foundTransactionSum = transactionRepository.sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
            from = UUID.fromString("2ab07e90-ebdc-4df4-8728-8aeeefcc6031"),
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(foundTransactionSum).isEqualTo(903.15)
    }

    @Test
    fun `should calculate amount sum of transactions by to and type and status and modified`() {
        val foundTransactionSum = transactionRepository.sumTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
            to = UUID.fromString("1b3d1d84-a234-4418-9cb5-82e9d721976c"),
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(foundTransactionSum).isEqualTo(858.16)
    }
}

