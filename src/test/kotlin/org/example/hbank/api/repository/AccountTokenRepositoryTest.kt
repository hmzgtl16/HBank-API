package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.*
import org.example.hbank.api.utility.AccountLimit
import org.example.hbank.api.utility.AccountStatus
import org.example.hbank.api.utility.AccountType
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
@TestPropertySource(
    properties = [
        "spring.datasource.url=jdbc:postgresql://localhost:5432/hbank_db",
        "spring.datasource.username=hbank_root",
        "spring.datasource.password=hbank_pass",
        "spring.datasource.driver-class-name=org.postgresql.Driver"
    ]
)
class AccountTokenRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var accountTokenRepository: AccountTokenRepository

    @Test
    fun `should find account token by token value`() {
        // Arrange
        val customer = createCustomer()
        entityManager.persist(customer)

        val account = createAccount(customer)
        entityManager.persist(account)

        val token = createToken("test-token-value")
        entityManager.persist(token)

        val accountToken = createAccountToken(account, token)
        entityManager.persist(accountToken)
        entityManager.flush()

        // Act
        val found = accountTokenRepository.findAccountTokenByTokenValue("test-token-value")

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.token?.value).isEqualTo("test-token-value")
        assertThat(found?.account?.id).isEqualTo(account.id)
    }

    @Test
    fun `should return null when account token token value does not exist`() {
        // Act
        val found = accountTokenRepository.findAccountTokenByTokenValue("nonexistent-token")

        // Assert
        assertThat(found).isNull()
    }

    @Test
    fun `should find account token by account`() {
        // Arrange
        val customer = createCustomer()
        entityManager.persist(customer)

        val account = createAccount(customer)
        entityManager.persist(account)

        val token = createToken("test-token-value")
        entityManager.persist(token)

        val accountToken = createAccountToken(account, token)
        entityManager.persist(accountToken)
        entityManager.flush()

        // Act
        val found = accountTokenRepository.findAccountTokenByAccount(account)

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.account?.id).isEqualTo(account.id)
        assertThat(found?.token?.value).isEqualTo("test-token-value")
    }

    @Test
    fun `should return null when no token exists for account`() {
        // Arrange
        val customer = createCustomer()
        entityManager.persist(customer)

        val account = createAccount(customer)
        entityManager.persist(account)
        entityManager.flush()

        // Act
        val found = accountTokenRepository.findAccountTokenByAccount(account)

        // Assert
        assertThat(found).isNull()
    }

    private fun createCustomer(): Customer {
        val user = createUser()
        entityManager.persist(user)

        return Customer(
            firstname = "John",
            lastname = "Doe",
            created = Instant.now(),
            modified = Instant.now(),
            user = user
        )
    }

    private fun createUser(): User {
        return User(
            username = "johndoe",
            email = "john.doe@example.com",
            password = "password123",
            created = Instant.now()
        )
    }

    private fun createAccount(
        customer: Customer,
        number: String = "1234567890",
        type: AccountType = AccountType.PERSONAL_ACCOUNT
    ): Account =
        Account(
            number = number,
            name = "${customer.firstname} ${customer.lastname}",
            balance = 1000.0,
            type = type,
            limit = AccountLimit.STANDARD_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED,
            created = Instant.now(),
            modified = Instant.now(),
            customer = customer
        )

    private fun createToken(value: String): Token =
        Token(value = value, created = Instant.now())

    private fun createAccountToken(account: Account, token: Token): AccountToken =
        AccountToken(token = token, account = account)
}
