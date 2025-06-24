package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.User
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
class CustomerRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @BeforeEach
    fun setUp() {
        val user = createUser()
        entityManager.persist(user)

        val customer = createCustomer(user)
        entityManager.persist(customer)
        entityManager.flush()
    }

    @Test
    fun `should find customer by username`() {
        val username = "johndoe"

        val found = customerRepository.findCustomerByUserUsername(username)

        assertThat(found).isNotNull
        assertThat(found?.user?.username).isEqualTo(username)
    }

    @Test
    fun `should return null when no customer exists for username`() {
        val found = customerRepository.findCustomerByUserUsername("nonexistent")

        assertThat(found).isNull()
    }

    @Test
    fun `should find customer by email`() {
        val email = "john.doe@example.com"

        val found = customerRepository.findCustomerByUserEmail(email)

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.user?.email).isEqualTo(email)
    }

    @Test
    fun `should return null when no customer exists for email`() {
        val found = customerRepository.findCustomerByUserEmail("nonexistent")

        assertThat(found).isNull()
    }

    @Test
    fun `should find customer by phone number`() {
        val phoneNumber = "010-1234-5678"

        val found = customerRepository.findCustomerByUserPhoneNumber(phoneNumber)

        assertThat(found).isNotNull
        assertThat(found?.user?.phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    fun `should return null when no customer exists for phone number`() {
        val found = customerRepository.findCustomerByUserPhoneNumber("nonexistent")

        assertThat(found).isNull()
    }

    private fun createUser(): User {
        return User(
            username = "johndoe",
            email = "john.doe@example.com",
            password = "password123",
            phoneNumber = "010-1234-5678",
            createdAt = Instant.now(),
            modifiedAt = Instant.now()
        )
    }

    private fun createCustomer(user: User): Customer {
        return Customer(
            firstname = "John",
            lastname = "Doe",
            createdAt = Instant.now(),
            modifiedAt = Instant.now(),
            user = user
        )
    }
}
