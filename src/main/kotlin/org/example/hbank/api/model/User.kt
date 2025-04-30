package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    name = "table_user", uniqueConstraints = [
        UniqueConstraint(name = "ukijm6au1nwxkpta71qvup7x2nw", columnNames = ["user_email_address"]),
        UniqueConstraint(name = "uk9v0ipmihebk2p0yj7wru85p1o", columnNames = ["user_phone_number"]),
        UniqueConstraint(name = "ukgh2jnueqrabc2vvpo7jbld12s", columnNames = ["user_username"])
    ]
)
class User(
    @Size(max = 255)
    @NotNull
    @Column(name = "user_email_address", nullable = false)
    var email: String,
    @Size(max = 20)
    @NotNull
    @Column(name = "user_username", nullable = false, length = 20)
    var username: String,
    @Size(max = 255)
    @NotNull
    @Column(name = "user_password", nullable = false)
    var password: String,
    @Size(max = 255)
    @Column(name = "user_phone_number")
    var phoneNumber: String? = null,
    @NotNull
    @Column(name = "user_created", nullable = false)
    var created: Instant
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    var id: UUID? = null

    @NotNull
    @Column(name = "user_enabled", nullable = false)
    var enabled: Boolean = false

}