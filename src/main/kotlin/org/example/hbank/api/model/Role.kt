package org.example.hbank.api.model


import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.util.*


@Entity
@Table(name = "table_role")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id") val id: UUID? = null,
    @Column(name = "role_name", nullable = false, unique = true) val name: String
) {


    @OneToMany(mappedBy = "role")
    val users: Set<UserRole> = hashSetOf()

    @ManyToMany
    @JoinTable(
        name = "table_role_privilege",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "privilege_id")]
    )
    val privileges: Set<Privilege> = hashSetOf()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Role

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   name = $name )"
    }
}