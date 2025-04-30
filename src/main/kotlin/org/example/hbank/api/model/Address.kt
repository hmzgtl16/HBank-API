package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@Table(name = "table_address")
class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id", nullable = false)
    var id: UUID? = null

    @Size(max = 255)
    @Column(name = "address_municipality")
    var municipality: String? = null

    @Size(max = 255)
    @Column(name = "address_province")
    var province: String? = null

    @Size(max = 255)
    @Column(name = "address_street")
    var street: String? = null

    @Column(name = "address_zip")
    var zip: String? = null
}