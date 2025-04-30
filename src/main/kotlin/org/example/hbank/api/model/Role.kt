package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@Table(
    name = "table_role", uniqueConstraints = [
        UniqueConstraint(name = "ukgx3jf15702y0aixdh41dqv4tk", columnNames = ["role_name"])
    ]
)
class Role(
    @Size(max = 255)
    @NotNull
    @Column(name = "role_name", nullable = false)
    var name: String? = null
) {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id", nullable = false)
    var id: UUID? = null
}