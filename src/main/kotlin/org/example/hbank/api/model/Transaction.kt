package org.example.hbank.api.model

import jakarta.persistence.*
import org.example.hbank.api.util.TransactionStatus
import org.example.hbank.api.util.TransactionType
import org.hibernate.proxy.HibernateProxy
import java.time.Instant
import java.util.*

@Entity
@Table(name = "table_transaction")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id") val id: UUID? = null,
    @Column(name = "transaction_reference", nullable = false, unique = true) val reference: UUID,
    @Column(name = "transaction_amount") val amount: Double,
    @Column(name = "transaction_fees") val fees: Double = 0.0,
    @Column(name = "transaction_type") val type: TransactionType,
    @Column(name = "transaction_status") val status: TransactionStatus,
    @Column(name = "transaction_created_at") val createdAt: Instant,
    @Column(name = "transaction_modified_at") val modifiedAt: Instant,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_from", nullable = false)
    val from: Account,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_to", nullable = false)
    val to: Account
) {


    @OneToMany(mappedBy = "transaction")
    val tokens: Set<TransactionToken> = hashSetOf()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Transaction

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   reference = $reference   ,   amount = $amount   ,   fees = $fees   ,   type = $type   ,   status = $status   ,   createdAt = $createdAt   ,   modifiedAt = $modifiedAt )"
    }
}
