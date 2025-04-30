package org.example.hbank.api.model

import org.example.hbank.api.utility.AccountLimit
import org.example.hbank.api.utility.AccountStatus
import org.example.hbank.api.utility.AccountType
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "table_account",
    uniqueConstraints = [
        UniqueConstraint(name = "uks21mjytpy5da54y28m54inuh2", columnNames = ["account_number"]),
        UniqueConstraint(name = "ukb5oe87k5y71lk7993r76b80ky", columnNames = ["customer_id"])
    ]
)
class Account(
    @Size(max = 255)
    @NotNull
    @Column(name = "account_number", nullable = false)
    var number: String,
    @Size(max = 255)
    @NotNull
    @Column(name = "account_name", nullable = false)
    var name: String,
    @NotNull
    @Column(name = "account_balance", nullable = false)
    var balance: Double = 0.0,
    @NotNull
    @Column(name = "account_type", nullable = false)
    var type: AccountType,
    @NotNull
    @Column(name = "account_limit", nullable = false)
    var limit: AccountLimit,
    @NotNull
    @Column(name = "account_status", nullable = false)
    var status: AccountStatus,
    @NotNull
    @Column(name = "account_created", nullable = false)
    var created: Instant,
    @NotNull
    @Column(name = "account_modified", nullable = false)
    var modified: Instant,
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: Customer
) {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id", nullable = false)
    var id: UUID? = null
}
