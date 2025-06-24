package org.example.hbank.api.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "table_user_role")
data class UserRole(
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "userId")
    @JoinColumn(name = "user_id")
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "roleId")
    @JoinColumn(name = "role_id")
    val role: Role
) {

    @EmbeddedId
    var id: UserRoleId = UserRoleId(user.id!!, role.id!!)

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as UserRole

        return id == other.id
    }

    final override fun hashCode(): Int = Objects.hash(id);

    override fun toString(): String {
        return this::class.simpleName + "(EmbeddedId = $id )"
    }
}

@Embeddable
data class UserRoleId(
    @Column(name = "user_id") val userId: UUID,
    @Column(name = "role_id") val roleId: UUID
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserRoleId) return false

        if (userId != other.userId) return false
        if (roleId != other.roleId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + roleId.hashCode()
        return result
    }
}
