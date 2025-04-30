package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "table_account_token")
class AccountToken(
    @MapsId("tokenId")
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(name = "token_id", nullable = false)
    val token: Token,
    @MapsId("accountId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "account_id",
        nullable = false
    )
    val account: Account
) : Serializable {
    @EmbeddedId
    var id: AccountTokenId? = null

    init {
        this.id = AccountTokenId(accountId = account.id!!, tokenId = token.id!!)
    }
}

@Embeddable
class AccountTokenId(
    @NotNull
    @Column(name = "account_id", nullable = false)
    val accountId: UUID,
    @NotNull
    @Column(name = "token_id", nullable = false)
    val tokenId: UUID
) : Serializable {

    override fun hashCode(): Int = Objects.hash(accountId, tokenId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as AccountTokenId

        return accountId == other.accountId &&
                tokenId == other.tokenId
    }

    companion object {
        private const val serialVersionUID = -1545475922446876555L
    }
}