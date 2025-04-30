package org.example.hbank.api.service

import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.utility.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TransferService : TransactionService() {

    fun createTransfer(
        from: Account,
        to: Account,
        amount: Double
    ): Transaction {

        val reference = generateTransactionReference()
        val transaction = Transaction(
            reference = reference,
            amount = amount,
            type = TransactionType.TRANSFER,
            status = TransactionStatus.CREATED,
            created = Instant.now(clock),
            modified = Instant.now(clock),
            from = from,
            to = to
        )
        
        return createTransaction(transaction = transaction)
    }

    fun reachDailyTransferLimit(
        account: Account,
        amount: Double
    ): Boolean {

        val transactionsAmountSum = sumDistinctTransactionsByTypeAndStatusAndFromAndCreatedIsBetween(
            from = account,
            start = Instant.now(clock).startOfTheMonth(),
            end = Instant.now(clock).endOfTheMonth()
        )
        return transactionsAmountSum.plus(other = amount) > account.limit.dailyTransfersAmountLimit
    }

    fun reachMonthlyTransferLimit(
        account: Account,
        amount: Double
    ): Boolean {

        val transactionsAmountSum = sumDistinctTransactionsByTypeAndStatusAndFromAndCreatedIsBetween(
            from = account,
            start = Instant.now(clock).startOfTheMonth(),
            end = Instant.now(clock).endOfTheMonth()
        )
        return transactionsAmountSum.plus(other = amount) > account.limit.monthlyTransfersAmountLimit
    }

    fun reachYearlyTransferLimit(
        account: Account,
        amount: Double
    ): Boolean {

        val transactionsAmountSum = sumDistinctTransactionsByTypeAndStatusAndFromAndCreatedIsBetween(
            from = account,
            start = Instant.now(clock).startOfTheMonth(),
            end = Instant.now(clock).endOfTheMonth()
        )
        return transactionsAmountSum.plus(other = amount) > account.limit.yearlyTransfersAmountLimit
    }

    fun reachDailyTransferNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            from = account,
            start = Instant.now(clock).startOfTheDay(),
            end = Instant.now(clock).endOfTheDay()
        ) >= account.limit.maximumDailyTransferNumber

    fun reachMonthlyTransferNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            from = account,
            start = Instant.now(clock).startOfTheMonth(),
            end = Instant.now(clock).endOfTheMonth()
        ) >= account.limit.maximumMonthlyTransferNumber

    fun reachYearlyTransferNumberLimit(account: Account): Boolean = transactionRepository
        .countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
            type = TransactionType.TRANSFER,
            status = listOf(TransactionStatus.COMPLETED),
            from = account,
            start = Instant.now(clock).startOfTheYear(),
            end = Instant.now(clock).endOfTheYear()
        ) >= account.limit.maximumYearlyTransferNumber

    private fun sumDistinctTransactionsByTypeAndStatusAndFromAndCreatedIsBetween(
        from: Account,
        start: Instant,
        end: Instant
    ): Double = transactionRepository.findDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
        type = TransactionType.TRANSFER,
        status = listOf(TransactionStatus.COMPLETED),
        from = from,
        start = start,
        end = end
    ).sumOf(Transaction::amount)

}
