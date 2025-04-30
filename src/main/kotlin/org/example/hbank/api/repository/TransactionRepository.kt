package org.example.hbank.api.repository

import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.utility.TransactionStatus
import org.example.hbank.api.utility.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*

interface TransactionRepository : JpaRepository<Transaction, UUID> {

    fun findTransactionByReference(reference: UUID): Transaction?

    fun existsTransactionByReference(reference: UUID): Boolean

    @Query("""select count(distinct t) from Transaction t where t.type = :type and t.status in :status and t.from = :from and t.modified between :start and :end""")
    fun countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
        @Param("from") from: Account,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): Long

    @Query("""select count(distinct t) from Transaction t where t.type = :type and t.status in :status and t.to = :to and t.modified between :start and :end""")
    fun countDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
        @Param("to") to: Account,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): Long

    @Query("""select distinct t from Transaction t where t.from = :from and t.type = :type and t.status in :status and t.modified between :start and :end""")
    fun findDistinctTransactionsByFromAndTypeAndStatusInAndModifiedBetween(
        @Param("from") from: Account,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): List<Transaction>

    @Query("""select distinct t from Transaction t where t.type = :type and t.status in :status and t.to = :to and t.modified between :start and :end""")
    fun findDistinctTransactionsByToAndTypeAndStatusInAndModifiedBetween(
        @Param("to") to: Account,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): List<Transaction>

    @Query("""select distinct t from Transaction t where (t.from = :account or t.to = :account) and t.modified between :start and :end order by t.created""")
    fun findTransactionsByAccountAndModifiedBetween(
        account: Account,
        start: Instant,
        end: Instant
    ): List<Transaction>
}
