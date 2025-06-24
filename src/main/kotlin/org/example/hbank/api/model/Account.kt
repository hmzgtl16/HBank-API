package org.example.hbank.api.model

import jakarta.persistence.*
import org.example.hbank.api.util.AccountLimit
import org.example.hbank.api.util.AccountStatus
import org.example.hbank.api.util.AccountType
import org.hibernate.proxy.HibernateProxy
import java.time.Instant
import java.util.*

@Entity
@Table(name = "table_account")
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id") val id: UUID? = null,
    @Column(name = "account_number", nullable = false, unique = true) val number: String,
    @Column(name = "account_name", nullable = false) val name: String,
    @Column(name = "account_balance", nullable = false) val balance: Double = 0.0,
    @Column(name = "account_type", nullable = false) val type: AccountType,
    @Column(name = "account_limit", nullable = false) val limit: AccountLimit,
    @Column(name = "account_status", nullable = false) val status: AccountStatus,
    @Column(name = "account_created_at", nullable = false) val createdAt: Instant,
    @Column(name = "account_modified_at", nullable = false) val modifiedAt: Instant,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_customer_id", unique = true, nullable = false, updatable = false)
    val customer: Customer
) {


    @OneToMany(mappedBy = "account")
    val tokens: Set<AccountToken> = hashSetOf()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Account

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   number = $number   ,   name = $name   ,   balance = $balance   ,   type = $type   ,   limit = $limit   ,   status = $status   ,   createdAt = $createdAt   ,   modifiedAt = $modifiedAt )"
    }
}
