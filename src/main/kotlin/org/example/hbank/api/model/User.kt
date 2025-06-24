package org.example.hbank.api.model


import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.Instant
import java.util.*

@Entity
@Table(name = "table_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id") val id: UUID? = null,
    @Column(name = "user_email", nullable = false, unique = true) val email: String,
    @Column(name = "user_username", nullable = false, unique = true) val username: String,
    @Column(name = "user_password", nullable = false) val password: String,
    @Column(name = "user_phone_number", unique = true) val phoneNumber: String? = null,
    @Column(name = "user_created_at", nullable = false) val createdAt: Instant,
    @Column(name = "user_modified_at", nullable = false) val modifiedAt: Instant,
    @Column(name = "user_enabled", nullable = false) val enabled: Boolean = false
) {


    @OneToMany(mappedBy = "user")
val roles: Set<UserRole> = hashSetOf()

    @OneToMany(mappedBy = "user")
    val tokens: Set<UserToken> = hashSetOf()


    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as User

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   email = $email   ,   username = $username   ,   password = $password   ,   phoneNumber = $phoneNumber   ,   createdAt = $createdAt   ,   modifiedAt = $modifiedAt   ,   enabled = $enabled )"
    }
}