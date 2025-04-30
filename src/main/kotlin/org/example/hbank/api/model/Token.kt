package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    name = "table_token", uniqueConstraints = [
        UniqueConstraint(name = "uk9bya605pxvxu83yr708rhly8o", columnNames = ["token_value"])
    ]
)
class Token(
    @Size(max = 255)
    @NotNull
    @Column(name = "token_value", nullable = false)
    var value: String,
    @NotNull
    @Column(name = "token_created", nullable = false)
    var created: Instant
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_id", nullable = false)
    var id: UUID? = null

}