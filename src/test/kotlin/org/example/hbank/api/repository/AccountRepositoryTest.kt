package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.User
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
class AccountRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Test
    fun `should find account by number`() {
        // Arrange
        val customer = createCustomer()
        entityManager.persist(customer)

        val account = createAccount(customer, "1234567890")
        entityManager.persist(account)
        entityManager.flush()

        // Act
        val found = accountRepository.findAccountByNumber("1234567890")

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.number).isEqualTo("1234567890")
    }

    @Test
    fun `should return null when account number does not exist`() {
        // Act
        val found = accountRepository.findAccountByNumber("nonexistent")

        // Assert
        assertThat(found).isNull()
    }

    @Test
    fun `should find account by customer and type`() {
        // Arrange
        val customer = createCustomer()
        entityManager.persist(customer)

        val account = createAccount(customer, "1234567890", AccountType.PERSONAL_ACCOUNT)
        entityManager.persist(account)
        entityManager.flush()

        // Act
        val found = accountRepository.findAccountByCustomerAndType(customer, AccountType.PERSONAL_ACCOUNT)

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.type).isEqualTo(AccountType.PERSONAL_ACCOUNT)
        assertThat(found?.customer?.id).isEqualTo(customer.id)
    }

    @Test
    fun `should return null when no account exists for customer and type`() {
        // Arrange
        val customer = createCustomer()
        entityManager.persist(customer)
        entityManager.flush()

        // Act
        val found = accountRepository.findAccountByCustomerAndType(customer, AccountType.PERSONAL_ACCOUNT)

        // Assert
        assertThat(found).isNull()
    }

    @Test
    fun `should check if account exists by number`() {
        // Arrange
        val customer = createCustomer()
        entityManager.persist(customer)

        val account = createAccount(customer, "1234567890")
        entityManager.persist(account)
        entityManager.flush()

        // Act & Assert
        assertThat(accountRepository.existsAccountByNumber("1234567890")).isTrue
        assertThat(accountRepository.existsAccountByNumber("nonexistent")).isFalse
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
        number: String,
        type: AccountType = AccountType.PERSONAL_ACCOUNT
    ): Account {
        return Account(
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
    }
}
