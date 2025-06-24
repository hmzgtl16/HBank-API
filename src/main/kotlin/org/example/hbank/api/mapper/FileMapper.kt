package org.example.hbank.api.mapper


import org.example.hbank.api.model.File
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile

@Component
class FileMapper {

    fun toEntity(multipartFile: MultipartFile): File = File(
        name = StringUtils.cleanPath(multipartFile.originalFilename.toString()),
        type = multipartFile.contentType.toString(),
        data = multipartFile.bytes
    )
}
