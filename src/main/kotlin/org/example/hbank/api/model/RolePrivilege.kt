package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "table_role_privilege")
class RolePrivilege(
    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,
    @MapsId("privilegeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "privilege_id", nullable = false)
    var privilege: Privilege
) {
    @EmbeddedId
    var id: RolePrivilegeId? = null

    init {
        this.id = RolePrivilegeId().also {
            it.privilegeId = privilege.id
            it.roleId = role.id
        }
    }
}

@Embeddable
class RolePrivilegeId : Serializable {
    @NotNull
    @Column(name = "privilege_id", nullable = false)
    var privilegeId: UUID? = null

    @NotNull
    @Column(name = "role_id", nullable = false)
    var roleId: UUID? = null

    override fun hashCode(): Int = Objects.hash(privilegeId, roleId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as RolePrivilegeId

        return privilegeId == other.privilegeId &&
                roleId == other.roleId
    }

    companion object {
        private const val serialVersionUID = -4017279319346320891L
    }
}