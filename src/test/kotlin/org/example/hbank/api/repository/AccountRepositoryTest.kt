package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.example.hbank.api.util.AccountType
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
class AccountRepositoryTest {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Test
    fun `should find account by number`() {
        val foundAccount = accountRepository.findAccountByNumber("ACCT-358632-91")

        assertThat(foundAccount).isNotNull
        assertThat(foundAccount!!).matches {
            it.number == "ACCT-358632-91" &&
                    it.name == "David Williams" &&
                    it.customer.id == UUID.fromString("db6b95de-051b-4282-92d6-a557d04182b9")
        }
    }

    @Test
    fun `should find account by username`() {
        val foundAccount =
            accountRepository.findAccountByCustomerUserUsername("alice_johnson_a7c3")

        assertThat(foundAccount).isNotNull
        assertThat(foundAccount!!).matches {
            it.number == "ACCT-725769-39" &&
                    it.name == "Alice Johnson" &&
                    it.customer.id == UUID.fromString("7d143fc6-6106-451a-8833-2293c9ebb633")
        }
    }

    @Test
    fun `should find account by username and type`() {
        val foundAccount = accountRepository
            .findAccountByCustomerUserUsernameAndType("george_brown_5785", AccountType.PERSONAL_ACCOUNT)

        assertThat(foundAccount).isNotNull
        assertThat(foundAccount!!).matches {
            it.number == "ACCT-200695-73" &&
                    it.name == "George Brown" &&
                    it.type == AccountType.PERSONAL_ACCOUNT &&
                    it.customer.id == UUID.fromString("cc471039-9b22-4804-a807-521ab2d6fb88")
        }
    }

    @Test
    fun `should find account by email and type`() {
        val foundAccount = accountRepository
            .findAccountByCustomerUserEmailAndType("george.brown@example.com", AccountType.PERSONAL_ACCOUNT)

        assertThat(foundAccount).isNotNull
        assertThat(foundAccount!!).matches {
            it.number == "ACCT-200695-73" &&
                    it.name == "George Brown" &&
                    it.type == AccountType.PERSONAL_ACCOUNT &&
                    it.customer.id == UUID.fromString("cc471039-9b22-4804-a807-521ab2d6fb88")
        }
    }

    @Test
    fun `should find account by phone number and type`() {
        val foundAccount = accountRepository
            .findAccountByCustomerUserPhoneNumberAndType("+1-457-810-7727", AccountType.PERSONAL_ACCOUNT)

        assertThat(foundAccount).isNotNull
        assertThat(foundAccount!!).matches {
            it.number == "ACCT-200695-73" &&
                    it.name == "George Brown" &&
                    it.type == AccountType.PERSONAL_ACCOUNT &&
                    it.customer.id == UUID.fromString("cc471039-9b22-4804-a807-521ab2d6fb88")
        }
    }

    @Test
    fun `should check if account exists by number`() {
        assertThat(accountRepository.existsAccountByNumber("ACCT-148408-52")).isTrue
        assertThat(accountRepository.existsAccountByNumber("ACCT-145685-74")).isFalse
    }
}
