package org.example.hbank.api.service

import kotlinx.datetime.toKotlinInstant
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.model.User
import org.example.hbank.api.response.RequestResponse
import org.example.hbank.api.utility.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RequestService : TransactionService() {

    fun createRequest(
        to: Account,
        from: Account,
        amount: Double
    ): Transaction {

        val reference = generateTransactionReference()
        val transaction = Transaction(
            reference = reference,
            amount = amount,
            type = TransactionType.REQUEST ,
            status = TransactionStatus.CREATED,
            created = Instant.now(clock),
            modified = Instant.now(clock),
            from = from,
            to = to,
        )

        return createTransaction(transaction = transaction)
    }

    fun reachDailyRequestLimit(
        account: Account,
        amount: Double
    ): Boolean {
        val transactionsAmountSum = sumTransactionsByToAndTypeAndStatusInAndCreatedBetween(
                to = account,
                start = Instant.now(clock).startOfTheDay(),
                end = Instant.now(clock).endOfTheDay()
            )

        return transactionsAmountSum.plus(other = amount) > account.limit.dailyRequestsAmountLimit
    }

    fun reachMonthlyRequestLimit(
        account: Account,
        amount: Double
    ): Boolean {
        val transactionsAmountSum = sumTransactionsByToAndTypeAndStatusInAndCreatedBetween(
                to = account,
                start = Instant.now(clock).startOfTheMonth(),
                end = Instant.now(clock).endOfTheMonth()
            )

        return transactionsAmountSum.plus(other = amount) > account.limit.monthlyRequestsAmountLimit
    }

    fun reachYearlyRequestLimit(
        account: Account,
        amount: Double
    ): Boolean {
        val transactionsAmountSum = sumTransactionsByToAndTypeAndStatusInAndCreatedBetween(
            to = account,
            start = Instant.now(clock).startOfTheYear(),
            end = Instant.now(clock).endOfTheYear()
        )

        return transactionsAmountSum.plus(other = amount) > account.limit.yearlyRequestsAmountLimit
    }

    fun reachDailyRequestNumberLimit(account: Account): Boolean =
        transactionRepository.countDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
            type = TransactionType.REQUEST,
            status = listOf(TransactionStatus.COMPLETED, TransactionStatus.CREATED),
            to = account,
            start = Instant.now(clock).startOfTheDay(),
            end = Instant.now(clock).endOfTheDay()
        ) >= account.limit.maximumDailyRequestNumber

    fun reachMonthlyRequestNumberLimit(account: Account): Boolean =
        transactionRepository.countDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
            type = TransactionType.REQUEST,
            status = listOf(TransactionStatus.COMPLETED, TransactionStatus.CREATED),
            to = account,
            start = Instant.now(clock).startOfTheMonth(),
            end = Instant.now(clock).endOfTheMonth()
        ) >= account.limit.maximumMonthlyRequestNumber

    fun reachYearlyRequestNumberLimit(account: Account): Boolean =
        transactionRepository.countDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
            type = TransactionType.REQUEST,
            status = listOf(TransactionStatus.COMPLETED, TransactionStatus.CREATED),
            to = account,
            start = Instant.now(clock).startOfTheYear(),
            end = Instant.now(clock).endOfTheYear()
        ) >= account.limit.maximumYearlyRequestNumber

    private fun sumTransactionsByToAndTypeAndStatusInAndCreatedBetween(
        to: Account,
        start: Instant,
        end: Instant
    ): Double = transactionRepository
        .findDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
            to = to,
            type = TransactionType.REQUEST,
            status = listOf(TransactionStatus.COMPLETED),
            start = start,
            end = end
        ).sumOf(Transaction::amount)

    fun isRequest(transaction: Transaction): Boolean =
        transaction.type.ordinal == TransactionType.REQUEST.ordinal

    fun isRequestRecipient(
        transaction: Transaction,
        user: User
    ): Boolean = transaction.to.customer.user.id == user.id

    fun isRequestSender(
        transaction: Transaction,
        user: User
    ): Boolean = transaction.from.customer.user.id == user.id

    fun asRequestResponse(transaction: Transaction): RequestResponse =
        RequestResponse(
            reference = transaction.reference.toString(),
            created = transaction.created.toKotlinInstant(),
            amount = transaction.amount,
            status = transaction.status.asRequestStatus(),
            recipient = transaction.to.asResponse()
        )
}
