package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.model.User
import org.example.hbank.api.utility.AccountLimit
import org.example.hbank.api.utility.AccountStatus
import org.example.hbank.api.utility.AccountType
import org.example.hbank.api.utility.TransactionStatus
import org.example.hbank.api.utility.TransactionType
import org.example.hbank.api.utility.endOfTheYear
import org.example.hbank.api.utility.startOfTheYear
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.random.Random

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
class TransactionRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var transactionRepository: TransactionRepository
    
    private lateinit var account1: Account
    private lateinit var account2: Account
    private lateinit var account3: Account
    private lateinit var account4: Account
    private lateinit var account5: Account
    private lateinit var account6: Account
    private lateinit var account7: Account

    private val uuid: List<UUID> = MutableList(21) {
        UUID.fromString("8c316a${String.format("%0" + 2 + "d", it+1)}-a841-42d2-85a4-18526d278ecc")
    }

    private val transactions = mutableListOf<Transaction>()

    @BeforeEach
    fun setUp() {

        account1 = createAccount(
            firstname = "John",
            lastname = "Doe",
            email = "john.doe@example.com",
            username = "johndoe",
            password = "password123",
            birthdate = LocalDate.of(1990, 1, 1),
            number = "1234567890",
            type = AccountType.PERSONAL_ACCOUNT,
            limit = AccountLimit.STANDARD_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED
        )
        entityManager.persist(account1)

        account2 = createAccount(
            firstname = "Jane",
            lastname = "Smith",
            email = "jane.smith@example.com",
            username = "janesmith",
            password = "pass456",
            birthdate = LocalDate.of(1985, 5, 10),
            number = "9876543210",
            type = AccountType.PERSONAL_ACCOUNT,
            limit = AccountLimit.STANDARD_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED
        )
        entityManager.persist(account2)

        account3 = createAccount(
            firstname = "Robert",
            lastname = "Jones",
            email = "robert.jones@example.com",
            username = "robertj",
            password = "securepass",
            birthdate = LocalDate.of(2002, 10, 22),
            number = "5551234567",
            type = AccountType.PERSONAL_ACCOUNT,
            limit = AccountLimit.PREMIUM_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED
        )
        entityManager.persist(account3)

        account4 = createAccount(
            firstname = "Alice",
            lastname = "Brown",
            email = "alice.brown@example.com",
            username = "aliceb",
            password = "alice123",
            birthdate = LocalDate.of(1998, 3, 15),
            number = "1112223334",
            type = AccountType.PERSONAL_ACCOUNT,
            limit = AccountLimit.PREMIUM_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED
        )
        entityManager.persist(account4)

        account5 = createAccount(
            firstname = "Michael",
            lastname = "Davis",
            email = "michael.davis@example.com",
            username = "michaeld",
            password = "pass987",
            birthdate = LocalDate.of(1976, 7, 8),
            number = "4445556667",
            type = AccountType.PERSONAL_ACCOUNT,
            limit = AccountLimit.STANDARD_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED
        )
        entityManager.persist(account5)

        account6 = createAccount(
            firstname = "Linda",
            lastname = "Wilson",
            email = "linda.wilson@example.com",
            username = "lindaw",
            password = "linda456",
            birthdate = LocalDate.of(1989, 12, 1),
            number = "7778889990",
            type = AccountType.PERSONAL_ACCOUNT,
            limit = AccountLimit.STANDARD_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED
        )
        entityManager.persist(account6)

        account7 = createAccount(
            firstname = "Christopher",
            lastname = "Garcia",
            email = "chris.garcia@example.com",
            username = "chrisg",
            password = "chrispass",
            birthdate = LocalDate.of(2004, 6, 20),
            number = "2223334445",
            type = AccountType.PERSONAL_ACCOUNT,
            limit = AccountLimit.PREMIUM_PERSONAL_ACCOUNT,
            status = AccountStatus.ACTIVATED
        )
        entityManager.persist(account7)

        val transaction1 = createTransaction(
            reference = uuid[0],
            amount = 1200.00,
            type = TransactionType.TRANSFER,
            from = account2,
            to = account5,
            created = Instant.parse("2024-07-03T16:52:42.890Z")
        )
        entityManager.persist(transaction1)
        transactions.add(transaction1)

        val transaction2 = createTransaction(
            reference = uuid[1],
            amount = 500.00,
            type = TransactionType.TRANSFER,
            from = account4,
            to = account1,
            created = Instant.parse("2024-09-27T23:01:05.456Z")
        )
        entityManager.persist(transaction2)
        transactions.add(transaction2)

        val transaction3 = createTransaction(
            reference = uuid[2],
            amount = 75.00,
            type = TransactionType.TRANSFER,
            from = account1,
            to = account6,
            created = Instant.parse("2024-08-20T09:38:27.123Z")
        )
        entityManager.persist(transaction3)
        transactions.add(transaction3)

        val transaction4 = createTransaction(
            reference = uuid[3],
            amount = 2000.00,
            type = TransactionType.TRANSFER,
            from = account4,
            to = account3,
            created = Instant.parse("2024-10-12T07:29:50.789Z")
        )
        entityManager.persist(transaction4)
        transactions.add(transaction4)

        val transaction5 = createTransaction(
            reference = uuid[4],
            amount = 300.00,
            type = TransactionType.REQUEST,
            from = account5,
            to = account7,
            created = Instant.parse("2025-03-02T15:11:29.123Z")
        )
        entityManager.persist(transaction5)
        transactions.add(transaction5)

        val transaction6 = createTransaction(
            reference = uuid[5],
            amount = 150.00,
            type = TransactionType.TRANSFER,
            from = account2,
            to = account7,
            created = Instant.parse("2024-01-15T05:30:10.123Z")
        )
        entityManager.persist(transaction6)
        transactions.add(transaction6)

        val transaction7 = createTransaction(
            reference = uuid[6],
            amount = 1000.00,
            type = TransactionType.TRANSFER,
            from = account3,
            to = account1,
            created = Instant.parse("2025-04-25T01:49:10.567Z")
        )
        entityManager.persist(transaction7)
        transactions.add(transaction7)

        val transaction8 = createTransaction(
            reference = uuid[7],
            amount = 250.00,
            type = TransactionType.REQUEST,
            from = account2,
            to = account4,
            created = Instant.parse("2025-02-19T20:25:45.890Z")
        )
        entityManager.persist(transaction8)
        transactions.add(transaction8)

        val transaction9 = createTransaction(
            reference = uuid[8],
            amount = 600.00,
            type = TransactionType.REQUEST,
            from = account1,
            to = account3,
            created = Instant.parse("2025-04-05T10:21:42.001Z")
        )
        entityManager.persist(transaction9)
        transactions.add(transaction9)

        val transaction10 = createTransaction(
            reference = uuid[9],
            amount = 1800.00,
            type = TransactionType.TRANSFER,
            from = account5,
            to = account2,
            created = Instant.parse("2025-03-15T06:04:13.456Z")
        )
        entityManager.persist(transaction10)
        transactions.add(transaction10)

        val transaction11 = createTransaction(
            reference = uuid[10],
            amount = 90.00,
            type = TransactionType.REQUEST,
            from = account3,
            to = account4,
            created = Instant.parse("2025-01-08T03:42:01.567Z")
        )
        entityManager.persist(transaction11)
        transactions.add(transaction11)

        val transaction12 = createTransaction(
            reference = uuid[11],
            amount = 120.00,
            type = TransactionType.TRANSFER,
            from = account2,
            to = account4,
            created = Instant.parse("2024-12-29T12:59:17.234Z")
        )
        entityManager.persist(transaction12)
        transactions.add(transaction12)

        val transaction13 = createTransaction(
            reference = uuid[12],
            amount = 400.00,
            type = TransactionType.REQUEST,
            from = account7,
            to = account5,
            created = Instant.parse("2024-05-18T19:23:34.234Z")
        )
        entityManager.persist(transaction13)
        transactions.add(transaction13)

        val transaction14 = createTransaction(
            reference = uuid[13],
            amount = 800.00,
            type = TransactionType.TRANSFER,
            from = account4,
            to = account1,
            created = Instant.parse("2024-04-01T08:00:00.001Z")
        )
        entityManager.persist(transaction14)
        transactions.add(transaction14)

        val transaction15 = createTransaction(
            reference = uuid[14],
            amount = 1600.00,
            type = TransactionType.TRANSFER,
            from = account1,
            to = account3,
            created = Instant.parse("2024-02-22T14:45:55.456Z")
        )
        entityManager.persist(transaction15)
        transactions.add(transaction15)

        val transaction16 = createTransaction(
            reference = uuid[15],
            amount = 200.00,
            type = TransactionType.TRANSFER,
            from = account5,
            to = account1,
            created = Instant.parse("2024-03-10T21:12:01.789Z")
        )
        entityManager.persist(transaction16)
        transactions.add(transaction16)

        val transaction17 = createTransaction(
            reference = uuid[16],
            amount = 1100.00,
            type = TransactionType.TRANSFER,
            from = account3,
            to = account2,
            created = Instant.parse("2025-04-18T17:05:26.234Z")
        )
        entityManager.persist(transaction17)
        transactions.add(transaction17)

        val transaction18 = createTransaction(
            reference = uuid[17],
            amount = 350.00,
            type = TransactionType.REQUEST,
            from = account2,
            to = account1,
            created = Instant.parse("2025-03-22T22:37:57.789Z")
        )
        entityManager.persist(transaction18)
        transactions.add(transaction18)

        val transaction19 = createTransaction(
            reference = uuid[18],
            amount = 700.00,
            type = TransactionType.REQUEST,
            from = account2,
            to = account1,
            created = Instant.parse("2024-11-05T18:16:33.001Z")
        )
        entityManager.persist(transaction19)
        transactions.add(transaction19)

        val transaction20 = createTransaction(
            reference = uuid[19],
            amount = 1400.00,
            type = TransactionType.TRANSFER,
            from = account4,
            to = account5,
            created = Instant.parse("2024-06-25T11:07:18.567Z")
        )
        entityManager.persist(transaction20)
        transactions.add(transaction20)

        entityManager.flush()
    }

    @Test
    fun `should find transaction by reference`() {

        // Act
        val found = transactionRepository.findTransactionByReference(uuid.first())

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.reference).isEqualTo(uuid.first())
    }

    @Test
    fun `should return null when no transaction reference does not exist`() {
        // Act
        val found = transactionRepository.findTransactionByReference(UUID.randomUUID())

        // Assert
        assertThat(found).isNull()
    }

    @Test
    fun `should check if transaction exists by reference`() {

        // Act & Assert
        assertThat(transactionRepository.existsTransactionByReference(uuid.first())).isTrue
        assertThat(transactionRepository.existsTransactionByReference(uuid.last())).isFalse
    }

    @Test
    fun `should count transactions by from and type and status and modified`() {

        val actual = transactions.count {

            it.from == account1 &&
            it.type == TransactionType.TRANSFER &&
            it.status == TransactionStatus.COMPLETED &&
            it.modified.isAfter(Instant.parse("2024-01-01T00:00:00.000Z")) &&
            it.modified.isBefore(Instant.parse("2024-12-31T23:59:59.999Z"))
        }.toLong()

        val expected = transactionRepository.countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
            from = account1,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(expected).isEqualTo(actual)
    }

     @Test
    fun `should count transactions by to and type and status and modified`() {

        val expected = transactions.count {

            it.to == account1 &&
            it.type == TransactionType.TRANSFER &&
            it.status == TransactionStatus.COMPLETED &&
            it.modified.isAfter(Instant.parse("2024-01-01T00:00:00.000Z")) &&
            it.modified.isBefore(Instant.parse("2024-12-31T23:59:59.999Z"))
        }.toLong()

        val actual = transactionRepository.countDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
            to = account1,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should find transactions by from and type and status and modified`() {

        val expected = transactions.filter {

            it.from == account1 &&
            it.type == TransactionType.TRANSFER &&
            it.status == TransactionStatus.COMPLETED &&
            it.modified.isAfter(Instant.parse("2024-01-01T00:00:00.000Z")) &&
            it.modified.isBefore(Instant.parse("2024-12-31T23:59:59.999Z"))
        }

        val actual = transactionRepository.findDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
            from = account1,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(actual).isNotEmpty
        assertThat(actual).hasSize(expected.size)
        assertThat(actual).containsAll(expected)
    }

    @Test
    fun `should return empty list when no transactions found by from and type and status and modified`() {

        val actual = transactionRepository.findDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
            from = account7,
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(actual).isEmpty()
    }

    @Test
    fun `should find transactions by to and type and status and modified`() {

        val expected = transactions.filter {

            it.to == account1 &&
            it.type == TransactionType.REQUEST &&
            it.status == TransactionStatus.COMPLETED &&
            it.modified.isAfter(Instant.parse("2024-01-01T00:00:00.000Z")) &&
            it.modified.isBefore(Instant.parse("2024-12-31T23:59:59.999Z"))
        }

        val actual = transactionRepository.findDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
            to = account1,
            type = TransactionType.REQUEST,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(actual).isNotEmpty
        assertThat(actual).hasSize(expected.size)
        assertThat(actual).containsAll(expected)
    }

    @Test
    fun `should return empty list when no transactions found by to and type and status and modified`() {

        val actual = transactionRepository.findDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
            to = account7,
            type = TransactionType.REQUEST,
            status = listOf(TransactionStatus.COMPLETED),
            start = Instant.parse("2024-01-01T00:00:00.000Z"),
            end = Instant.parse("2024-12-31T23:59:59.999Z")
        )

        assertThat(actual).isEmpty()
    }

    @Test
    fun `should find transactions by account and modified`() {

        val expected = transactions.filter {

            (it.to == account1 || it.from == account1) &&
            it.modified.isAfter(Instant.parse("2025-01-01T00:00:00.000Z")) &&
            it.modified.isBefore(Instant.parse("2025-04-30T15:00:16.5687Z"))
        }

        val actual = transactionRepository.findTransactionsByAccountAndModifiedBetween(
            account = account1,
            start = Instant.parse("2025-01-01T00:00:00.000Z"),
            end = Instant.parse("2025-04-30T15:00:16.5687Z")
        )

        assertThat(actual).isNotEmpty
        assertThat(actual).hasSize(expected.size)
        assertThat(actual).containsAll(expected)
    }

    @Test
    fun `should return empty list when no transactions found by account and modified`() {

        val actual = transactionRepository.findTransactionsByAccountAndModifiedBetween(
            account = account6,
            start = Instant.parse("2025-04-01T00:00:00.000Z"),
            end = Instant.parse("2025-04-30T15:00:16.5687Z")
        )

        assertThat(actual).isEmpty()
    }

    private fun createUser(
        email: String,
        username: String,
        password: String
    ): User =
        User(
            email = email,
            username = username,
            password = password,
            created = Instant.parse("2025-04-30T15:00:16.5687Z")
        )

    private fun createCustomer(
        firstname: String,
        lastname: String,
        email: String,
        username: String,
        password: String,
        birthdate: LocalDate
    ): Customer {
        val user = createUser(
            username = username,
            email = email,
            password = password
        )
        entityManager.persist(user)

        return Customer(
            firstname = firstname,
            lastname = lastname,
            birthdate = birthdate,
            created = Instant.parse("2025-04-30T15:00:16.5687Z"),
            modified = Instant.parse("2025-04-30T15:00:16.5687Z"),
            user = user
        )
    }

    private fun createAccount(
        firstname: String,
        lastname: String,
        email: String,
        username: String,
        password: String,
        birthdate: LocalDate,
        number: String,
        type: AccountType,
        limit: AccountLimit,
        status: AccountStatus
    ): Account {
        val customer = createCustomer(
            firstname = firstname,
            lastname = lastname,
            email = email,
            username = username,
            password = password,
            birthdate = birthdate
        )
        entityManager.persist(customer)

        return Account(
            number = number,
            name = "${customer.firstname} ${customer.lastname}",
            balance = 1000.0,
            type = type,
            limit = limit,
            status = status,
            created = Instant.parse("2025-04-30T15:00:16.5687Z"),
            modified = Instant.parse("2025-04-30T15:00:16.5687Z"),
            customer = customer
        )
    }

    private fun createTransaction(
        reference: UUID,
        amount: Double,
        type: TransactionType,
        from: Account,
        to: Account,
        created: Instant,
        modified: Instant = created.plus(Random.nextLong(1, 3000000), ChronoUnit.SECONDS),
    ): Transaction =
        Transaction(
            reference = reference,
            amount = amount,
            type = type,
            status = TransactionStatus.COMPLETED,
            created = created,
            modified = modified,
            from = from,
            to = to
        )
}

