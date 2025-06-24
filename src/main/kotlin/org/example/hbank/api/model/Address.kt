package org.example.hbank.api.model

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Entity
@Table(name = "table_address")
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id") val id: UUID? = null,
    @Column(name = "address_municipality") val municipality: String,
    @Column(name = "address_province") val province: String,
    @Column(name = "address_street") val street: String,
    @Column(name = "address_zip") val zip: String
) {

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Address

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   municipality = $municipality   ,   province = $province   ,   street = $street   ,   zip = $zip )"
    }
}