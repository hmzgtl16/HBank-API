package org.example.hbank.api.service

import org.example.hbank.api.model.File
import org.example.hbank.api.repository.FileRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileService(
    private val fileRepository: FileRepository
) {

    fun saveFile(entity: File) = fileRepository.save(entity)

    fun getFileById(id: UUID) = fileRepository.findFileById(id = id)

}
