package org.example.hbank.api.repository

import org.example.hbank.api.model.File
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FileRepository : JpaRepository<File, UUID> {

    fun findFileById(id: UUID): File?
}