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
import org.hibernate.proxy.HibernateProxy
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "table_account_token")
data class AccountToken(
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "accountId")
    @JoinColumn(name = "account_id")
    val account: Account,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @MapsId(value = "tokenId")
    @JoinColumn(name = "token_id")
    val token: Token
) {

    @EmbeddedId
    var id: AccountTokenId = AccountTokenId(account.id!!, token.id!!)

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as AccountToken

        return id == other.id
    }

    final override fun hashCode(): Int = Objects.hash(id);

    override fun toString(): String {
        return this::class.simpleName + "(EmbeddedId = $id )"
    }

}

@Embeddable
data class AccountTokenId(
    @Column(name = "account_id") val accountId: UUID,
    @Column(name = "token_id") val tokenId: UUID
): Serializable