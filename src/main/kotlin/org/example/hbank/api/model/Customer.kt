package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(
    name = "table_customer", uniqueConstraints = [
        UniqueConstraint(name = "uktclr3py2ufm46h34codf4kh8f", columnNames = ["address_id"]),
        UniqueConstraint(name = "ukd2dx1i9vju7nc0g6mtqeuly19", columnNames = ["avatar_id"]),
        UniqueConstraint(name = "ukrxmtouhh3ealchoymptvm5oo6", columnNames = ["user_id"])
    ]
)
class Customer(
    @Size(max = 255)
    @Column(name = "customer_firstname")
    var firstname: String = "",
    @Size(max = 255)
    @Column(name = "customer_lastname")
    var lastname: String = "",
    @Column(name = "customer_birthdate")
    var birthdate: LocalDate? = null,
    @NotNull
    @Column(name = "customer_created", nullable = false)
    var created: Instant,
    @NotNull
    @Column(name = "customer_modified", nullable = false)
    var modified: Instant,
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id", nullable = false)
    var id: UUID? = null

    @NotNull
    @Column(name = "customer_verified", nullable = false)
    var verified: Boolean? = false

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    var address: Address? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    var avatar: File? = null
}