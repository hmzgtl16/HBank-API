package org.example.hbank.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@Table(name = "table_file")
class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id", nullable = false)
    var id: UUID? = null

    @NotNull
    @Lob
    @Column(name = "file_data", nullable = false)
    var data: ByteArray? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "file_name", nullable = false)
    var name: String? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "file_type", nullable = false)
    var type: String? = null
}