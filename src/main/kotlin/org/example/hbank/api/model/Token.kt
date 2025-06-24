package org.example.hbank.api.model

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.Instant
import java.util.*

@Entity
@Table(name = "table_token")
data class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_id") val id: UUID? = null,
    @Column(name = "token_value", nullable = false, unique = true) val value: String,
    @Column(name = "token_created_at", nullable = false) val createdAt: Instant
) {


    @OneToMany(mappedBy = "token")
    val users: Set<UserToken> = hashSetOf()

    @OneToMany(mappedBy = "token")
    val accounts: Set<AccountToken> = hashSetOf()

    @OneToMany(mappedBy = "token")
    val transactions: Set<TransactionToken> = hashSetOf()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Token

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   value = $value   ,   createdAt = $createdAt )"
    }
}