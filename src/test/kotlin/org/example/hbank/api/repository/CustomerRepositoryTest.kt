package org.example.hbank.api.repository

import org.example.hbank.api.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:postgresql://localhost:5432/hbank_db",
    "spring.datasource.username=hbank_root",
    "spring.datasource.password=hbank_pass",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
])
class CustomerRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Test
    fun `should find customer by user`() {
        // Arrange
        val user = createUser()
        entityManager.persist(user)

        val customer = createCustomer(user)
        entityManager.persist(customer)
        entityManager.flush()

        // Act
        val found = customerRepository.findCustomerByUser(user)

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(customer.id)
        assertThat(found?.user?.id).isEqualTo(user.id)
    }

    @Test
    fun `should return null when no customer exists for user`() {
        // Arrange
        val user = createUser()
        entityManager.persist(user)
        entityManager.flush()

        // Act
        val found = customerRepository.findCustomerByUser(user)

        // Assert
        assertThat(found).isNull()
    }

    private fun createUser(): User {
        return User(
            username = "johndoe",
            email = "john.doe@example.com",
            password = "password123",
            created = Instant.now()
        )
    }

    private fun createCustomer(user: User): Customer {
        return Customer(
            firstname = "John",
            lastname = "Doe",
            created = Instant.now(),
            modified = Instant.now(),
            user = user
        )
    }
}
