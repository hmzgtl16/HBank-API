package org.example.hbank.api.service

import org.example.hbank.api.utility.AccountLimit
import org.example.hbank.api.utility.AccountStatus
import org.example.hbank.api.utility.AccountType
import org.example.hbank.api.utility.Generator
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.repository.AccountRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class AccountService(
    private val clock: Clock,
    private val accountRepository: AccountRepository
) {

    fun createPersonalAccount(customer: Customer): Account {
        var number: String
        do {
            number = generateAccountNumber()
        } while (accountRepository.existsAccountByNumber(number = number))
        val name = "${customer.firstname} ${customer.lastname}"
        return saveAccount(
            account = Account(
                number = generateAccountNumber(),
                name = name,
                type = AccountType.PERSONAL_ACCOUNT,
                limit = AccountLimit.STANDARD_PERSONAL_ACCOUNT,
                status = AccountStatus.ACTIVATED,
                created = Instant.now(clock),
                modified = Instant.now(clock),
                customer = customer
            )
        )
    }

    fun saveAccount(account: Account): Account = accountRepository.save(account)

    fun saveAccounts(account1: Account, account2: Account) {
        accountRepository.saveAll(mutableListOf(account1, account2))
    }

    fun getCustomerPersonalAccount(customer: Customer): Account? = accountRepository
        .findAccountByCustomerAndType(customer = customer, type = AccountType.PERSONAL_ACCOUNT)

    fun getAccountByNumber(number: String): Account? = accountRepository
        .findAccountByNumber(number = number)

    fun isSameAccount(account1: Account, account2: Account) = account1.id == account2.id

    fun isInactiveAccount(account: Account): Boolean = account.status == AccountStatus.DEACTIVATED

    fun isBalanceSufficient(
        account: Account,
        amount: Double
    ): Boolean = account.balance >= amount

    fun exchangeMoney(transaction: Transaction) {
        transaction.apply {
            from.balance -= if (from.balance >= amount + fees) {
                amount + fees
            } else {
                amount + fees + 100.0
            }

            to.balance += amount

            from.modified = Instant.now(clock)
            to.modified = Instant.now(clock)

            saveAccounts(
                account1 = from,
                account2 = to
            )
        }
    }

    fun generateAccountNumber(): String {

        var generatedValue: String

        do {
            generatedValue = Generator.generateHexString(length = 10)
        } while (accountRepository.existsAccountByNumber(number = generatedValue))
        return generatedValue

    }
}
