package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "table_user_reset_password_token")
class UserResetPasswordToken(
    @MapsId("tokenId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], optional = false)
    @JoinColumn(name = "token_id", nullable = false)
    var token: Token,
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
) {
    @EmbeddedId
    var id: UserResetPasswordTokenId? = null

    init {
        this.id = UserResetPasswordTokenId(tokenId = token.id!!, userId = user.id!!)
    }
}

@Embeddable
class UserResetPasswordTokenId(
    @NotNull
    @Column(name = "token_id", nullable = false)
    var tokenId: UUID,
    @NotNull
    @Column(name = "user_id", nullable = false)
    var userId: UUID
) : Serializable {

    override fun hashCode(): Int = Objects.hash(tokenId, userId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as UserResetPasswordTokenId

        return tokenId == other.tokenId &&
                userId == other.userId
    }

    companion object {
        private const val serialVersionUID = -8738376848245462634L
    }
}