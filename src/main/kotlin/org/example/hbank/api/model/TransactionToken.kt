package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "table_transaction_token")
class TransactionToken {
    @EmbeddedId
    var id: TransactionTokenId? = null

    @MapsId("tokenId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "token_id", nullable = false)
    var token: Token? = null

    @MapsId("transactionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    var transaction: Transaction? = null
}

@Embeddable
class TransactionTokenId : Serializable {
    @NotNull
    @Column(name = "token_id", nullable = false)
    var tokenId: UUID? = null

    @NotNull
    @Column(name = "transaction_id", nullable = false)
    var transactionId: UUID? = null
    override fun hashCode(): Int = Objects.hash(tokenId, transactionId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as TransactionTokenId

        return tokenId == other.tokenId &&
                transactionId == other.transactionId
    }

    companion object {
        private const val serialVersionUID = 7687200920688419055L
    }
}