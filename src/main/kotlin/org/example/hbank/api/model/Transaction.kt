package org.example.hbank.api.model

import org.example.hbank.api.utility.TransactionStatus
import org.example.hbank.api.utility.TransactionType
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "table_transaction",
    uniqueConstraints = [
        UniqueConstraint(name = "uk6rluw64hkwirwnw972t0flge8", columnNames = ["transaction_reference"])
    ]
)
class Transaction(
    @NotNull
    @Column(name = "transaction_amount", nullable = false)
    var amount: Double,
    @NotNull
    @Column(name = "transaction_created", nullable = false)
    var created: Instant,
    @NotNull
    @Column(name = "transaction_modified", nullable = false)
    var modified: Instant,
    @NotNull
    @Column(name = "transaction_fees", nullable = false)
    var fees: Double = 0.0,
    @NotNull
    @Column(name = "transaction_reference", nullable = false)
    var reference: UUID,
    @NotNull
    @Column(name = "transaction_status", nullable = false)
    var status: TransactionStatus,
    @NotNull
    @Column(name = "transaction_type", nullable = false)
    var type: TransactionType,
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_from", nullable = false)
    var from: Account,
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_to", nullable = false)
    var to: Account
) {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", nullable = false)
    var id: UUID? = null

}