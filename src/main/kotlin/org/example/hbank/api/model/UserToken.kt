package org.example.hbank.api.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import org.example.hbank.api.util.TokenType
import org.hibernate.proxy.HibernateProxy
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "table_user_token")
data class UserToken(
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "userId")
    @JoinColumn(name = "user_id")
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @MapsId(value = "tokenId")
    @JoinColumn(name = "token_id")
    val token: Token,
    @Column(name = "type")
    val type: TokenType
) {

    @EmbeddedId
    var id: UserTokenId = UserTokenId(user.id!!, token.id!!)


    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as UserToken

        return id == other.id
    }

    final override fun hashCode(): Int = Objects.hash(id);

    override fun toString(): String {
        return this::class.simpleName + "(EmbeddedId = $id   ,   user = $user   ,   token = $token   ,   type = $type )"
    }
}

@Embeddable
data class UserTokenId(
    @Column(name = "user_id") val userId: UUID,
    @Column(name = "token_id") val tokenId: UUID
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserTokenId

        if (userId != other.userId) return false
        if (tokenId != other.tokenId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + tokenId.hashCode()
        return result
    }
}
