package org.example.hbank.api.controller

import com.waseetpay.api.response.CustomerResponse
import org.example.hbank.api.model.File
import org.example.hbank.api.service.CustomerService
import org.example.hbank.api.service.FileService
import org.example.hbank.api.service.UserService
import org.example.hbank.api.utility.Errors
import org.example.hbank.api.utility.asResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

@RestController
@RequestMapping(path = ["api/v1/customer"])
class CustomerController(
    transactionManager: PlatformTransactionManager,
    private val customerService: CustomerService,
    private val userService: UserService,
    private val fileService: FileService
) {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCustomerPersonalInfo(): ResponseEntity<CustomerResponse> {

        val response = transactionTemplate.execute {

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            val customer = customerService.getCustomerByUser(user = user)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

            customer.asResponse()
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

        return ResponseEntity.ok(response)
    }

    @GetMapping(
        path = ["avatar/{id}"],
        produces = [MediaType.IMAGE_PNG_VALUE]
    )
    fun getCustomerAvatarById(
        @PathVariable(name = "id") id: String
    ): ResponseEntity<ByteArray> {

        val idAsUUID = UUID.fromString(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.FILE_NOT_FOUND)

        val avatar = fileService.getFileById(id = idAsUUID)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.FILE_NOT_FOUND)

        val data = avatar.data ?: byteArrayOf()

        return ResponseEntity.ok(data)
    }

    @PostMapping(
        path = ["avatar/update"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateCustomerAvatar(
        @RequestParam(name = "avatar") avatar: MultipartFile
    ): ResponseEntity<CustomerResponse> {

        transactionTemplate.executeWithoutResult {

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            val customer = customerService.getCustomerByUser(user = user)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

            val file = File().also {
                it.name = StringUtils.cleanPath(avatar.originalFilename.toString())
                it.type = avatar.contentType.toString()
                it.data = avatar.bytes

                fileService.saveFile(entity = it)
            }

            customerService.updateCustomerAvatar(customer = customer, avatar = file)
        }

        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        path = ["outdated"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun isOutdated(
        @RequestParam(name = "modified")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        modified: Instant
    ): ResponseEntity<Boolean> {

        val isOutdated = transactionTemplate.execute {

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            val customer = customerService.getCustomerByUser(user = user)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

            customer.modified.isAfter(modified)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

        return ResponseEntity.ok(isOutdated)
    }
}
