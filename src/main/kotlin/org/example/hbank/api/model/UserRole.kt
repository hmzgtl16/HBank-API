package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "table_user_role")
class UserRole(
    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
) {
    @EmbeddedId
    var id: UserRoleId? = null

    init {
        this.id = UserRoleId(roleId = role.id!!, userId = user.id!!)
    }
}

@Embeddable
class UserRoleId(
    @NotNull
    @Column(name = "role_id", nullable = false)
    var roleId: UUID,
    @NotNull
    @Column(name = "user_id", nullable = false)
    var userId: UUID
) : Serializable {

    override fun hashCode(): Int = Objects.hash(roleId, userId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as UserRoleId

        return roleId == other.roleId &&
                userId == other.userId
    }

    companion object {
        private const val serialVersionUID = 9152642501005856218L
    }
}