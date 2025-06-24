package org.example.hbank.api.repository

import org.example.hbank.api.model.Transaction
import org.example.hbank.api.util.TransactionStatus
import org.example.hbank.api.util.TransactionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*

interface TransactionRepository : JpaRepository<Transaction, UUID> {

    override fun findAll(pageable: Pageable): Page<Transaction>

    @Query(
        """select distinct t from Transaction t
        where (t.from.customer.user.username = :username or t.to.customer.user.username = :username) and t.status in :status"""
    )
    fun findDistinctTransactionsByFromCustomerUserUsernameOrToCustomerUserUsernameAndStatusIn(
        @Param("username") username: String,
        @Param("status") status: List<TransactionStatus>,
        pageable: Pageable
    ): Page<Transaction>

    fun findTransactionByReference(reference: UUID): Transaction?

    fun existsTransactionByReference(reference: UUID): Boolean

    @Query(
        """select count(distinct t) from Transaction t
        where t.from.id = :from and t.type = :type and t.status in :status and t.modifiedAt between :start and :end"""
    )
    fun countDistinctTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
        @Param("from") from: UUID,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): Long

    @Query(
        """select count(distinct t) from Transaction t
        where t.to.id = :to and t.type = :type and t.status in :status and t.modifiedAt between :start and :end"""
    )
    fun countDistinctTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
        @Param("to") to: UUID,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): Long

    @Query(
        """select coalesce(sum (t.amount), 0)  from Transaction t
        where t.from.id = :from and t.type = :type and t.status in :status and t.modifiedAt between :start and :end"""
    )
    fun sumTransactionsByFromAndTypeAndStatusInAndModifiedAtBetween(
        @Param("from") from: UUID,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): Double

    @Query(
        """select coalesce(sum (t.amount), 0) from Transaction t
        where t.to.id = :to and t.type = :type and t.status in :status and t.modifiedAt between :start and :end"""
    )
    fun sumTransactionsByToAndTypeAndStatusInAndModifiedAtBetween(
        @Param("to") to: UUID,
        @Param("type") type: TransactionType,
        @Param("status") status: List<TransactionStatus>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): Double

}
