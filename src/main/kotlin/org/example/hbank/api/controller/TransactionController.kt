package org.example.hbank.api.controller

import org.example.hbank.api.model.Transaction
import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.service.AccountService
import org.example.hbank.api.service.CustomerService
import org.example.hbank.api.service.TransactionService
import org.example.hbank.api.service.UserService
import org.example.hbank.api.utility.Errors
import org.example.hbank.api.utility.asResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping(path = ["api/v1/transaction"])
class TransactionController(
    transactionManager: PlatformTransactionManager,
    private val userService: UserService,
    private val customerService: CustomerService,
    private val accountService: AccountService,
    private val transactionService: TransactionService
) {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    @PostMapping(
        path = ["last"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getLastTransactions(
        @RequestParam(name = "changed")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        changed: Instant,
    ): ResponseEntity<List<TransactionResponse>> {

        val response = transactionTemplate.execute {

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            val customer = customerService.getCustomerByUser(user)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

            val account = accountService.getCustomerPersonalAccount(customer)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.ACCOUNT_NOT_FOUND)

            transactionService
                .getLastTransactions(account = account, changed = changed)
                .map( Transaction::asResponse)
        } ?: listOf()

        return ResponseEntity.ok(response)
    }
}

