package org.example.hbank.api.service

import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.repository.CustomerRepository
import org.example.hbank.api.repository.FileRepository
import org.example.hbank.api.repository.TransactionRepository
import org.example.hbank.api.utility.TransactionStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
@Primary
class TransactionService {

    @Autowired lateinit var clock: Clock

    @Autowired lateinit var transactionRepository: TransactionRepository

    @Autowired lateinit var customerRepository: CustomerRepository

    @Autowired lateinit var fileRepository: FileRepository

    private fun saveTransaction(transaction: Transaction): Transaction = transactionRepository.save(transaction)

    fun createTransaction(transaction: Transaction): Transaction = saveTransaction(transaction = transaction)

    fun completeTransaction(transaction: Transaction): Transaction {
        transaction.status = TransactionStatus.COMPLETED
        transaction.modified = Instant.now(clock)
        return saveTransaction(transaction = transaction)
    }

    fun acceptTransaction(transaction: Transaction): Transaction {
        transaction.status = TransactionStatus.ACCEPTED
        transaction.modified = Instant.now(clock)
        return saveTransaction(transaction = transaction)
    }

    fun declineTransaction(transaction: Transaction): Transaction {
        transaction.status = TransactionStatus.DECLINED
        transaction.modified = Instant.now(clock)
        return saveTransaction(transaction = transaction)
    }

    fun cancelTransaction(transaction: Transaction): Transaction {
        transaction.status = TransactionStatus.CANCELED
        transaction.modified = Instant.now(clock)
        return saveTransaction(transaction = transaction)
    }

    fun generateTransactionReference(): UUID {
        var reference: UUID
        do {
            reference = UUID.randomUUID()
        } while (transactionRepository.existsTransactionByReference(reference = reference))

        return reference
    }

    fun getTransactionByReference(reference: UUID): Transaction? =
        transactionRepository.findTransactionByReference(reference = reference)

    fun addTransactionFees(transaction: Transaction): Transaction {

        var fees = when (transaction.amount.toInt()) {
            in 0 until 10000 -> 10.0
            in 10000 until 20000 -> transaction.amount.times(other = 0.0005).plus(other = 10.0)
            in 20000 until 50000 -> transaction.amount.times(other = 0.0008).plus(other = 10.0)
            in 50000 until 80000 -> transaction.amount.times(other = 0.001).plus(other = 10.0)
            in 80000 until 100000 -> transaction.amount.times(other = 0.0015).plus(other = 10.0)
            else -> transaction.amount.times(other = 0.0025).plus(other = 10.0)
        }
        fees = BigDecimal(fees).setScale(2, RoundingMode.HALF_UP).toDouble()
        transaction.fees = fees

        return saveTransaction(transaction = transaction)
    }

    fun reachTransactionAmountLimit(account: Account, amount: Double): Boolean =
        amount !in account.limit.minimumTransactionAmount..account.limit.maximumTransactionAmount

    fun getLastTransactions(account: Account, changed: Instant): List<Transaction> = transactionRepository
        .findTransactionsByAccountAndModifiedBetween(
            account = account,
            start = changed,
            end = Instant.now(clock)
        )


}
