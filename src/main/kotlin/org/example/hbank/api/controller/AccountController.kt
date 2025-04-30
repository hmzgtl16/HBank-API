package org.example.hbank.api.controller

import org.example.hbank.api.response.PersonalAccountResponse
import org.example.hbank.api.service.AccountService
import org.example.hbank.api.service.CustomerService
import org.example.hbank.api.service.TokenService
import org.example.hbank.api.service.UserService
import org.example.hbank.api.utility.asPersonalAccountResponse
import org.example.hbank.api.utility.Errors
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping(path = ["api/v1/account"])
class AccountController(
    transactionManager: PlatformTransactionManager,
    private val userService: UserService,
    private val customerService: CustomerService,
    private val accountService: AccountService,
    private val tokenService: TokenService
) {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    @GetMapping(
        path = ["personal"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getPersonalAccount(): ResponseEntity<PersonalAccountResponse> {

        val response = transactionTemplate.execute {

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            val customer = customerService.getCustomerByUser(user = user)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

            val account = accountService.getCustomerPersonalAccount(customer = customer)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.PERSONAL_ACCOUNT_NOT_FOUND)

            val token = tokenService.getOrCreateAccountToken(account = account).token

            account.asPersonalAccountResponse(token = token.value)

        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.PERSONAL_ACCOUNT_NOT_FOUND)

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["personal/outdated"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun isOutdated(
        @RequestParam(name = "modified")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        modified: Instant,
    ): ResponseEntity<Boolean> {

        val isOutdated = transactionTemplate.execute {

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            val customer = customerService.getCustomerByUser(user = user)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

            val account = accountService.getCustomerPersonalAccount(customer = customer)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.PERSONAL_ACCOUNT_NOT_FOUND)

            account.modified.isAfter(modified)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.PERSONAL_ACCOUNT_NOT_FOUND)

        return ResponseEntity.ok(isOutdated)
    }

    /* @GetMapping(
        path = ["token/{value}"],
        produces = [MediaType.IMAGE_PNG_VALUE]
    )
    fun generateToken(@PathVariable value: String): Resource {
        val qrCodeFile = QRCode.generateQRCodeFile(content = value)
        return ByteArrayResource(qrCodeFile)
    }*/

    /*
        fun generateToken(): ResponseEntity<FileResponse> {
            val user = userService.getCurrentUser()
            val customer = customerService.getCustomerByUser(user = user)
            val account = accountService.getCustomerPersonalAccount(customer = customer)
            val accountToken = accountService.createAccountToken(account = account)
            QRCodeUtility.generateQRCodeFile(content = accountToken.value, filename = accountToken.id.toString())
            return ResponseEntity(
                FileResponse(
                    ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/images/")
                        .path(accountToken.id.toString())
                        .path(".png")
                        .toUriString()
                ),
                HttpStatus.OK
            )
        }
    */
}
