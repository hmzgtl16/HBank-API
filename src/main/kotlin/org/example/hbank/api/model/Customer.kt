package org.example.hbank.api.model

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "table_customer")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id") val id: UUID? = null,
    @Column(name = "customer_first_name") val firstname: String? = null,
    @Column(name = "customer_last_name") val lastname: String? = null,
    @Column(name = "customer_birthdate") val birthdate: LocalDate? = null,
    @Column(name = "customer_verified", nullable = false) val verified: Boolean = false,
    @Column(name = "customer_created_at", nullable = false) val createdAt: Instant,
    @Column(name = "customer_modified_at", nullable = false) val modifiedAt: Instant,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_id", unique = true, nullable = false, updatable = false)
    val user: User,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_address_id", unique = true)
    val address: Address? = null,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_file_id", unique = true)
    val avatar: File? = null
) {


    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Customer

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   firstname = $firstname   ,   lastname = $lastname   ,   birthdate = $birthdate   ,   verified = $verified   ,   createdAt = $createdAt   ,   modifiedAt = $modifiedAt )"
    }
}