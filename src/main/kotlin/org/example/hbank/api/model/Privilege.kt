package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@Table(name = "table_privilege")
class Privilege(
    @Size(max = 255)
    @NotNull
    @Column(name = "privilege_name", nullable = false)
    var name: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "privilege_id", nullable = false)
    var id: UUID? = null

}