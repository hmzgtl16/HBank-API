package org.example.hbank.api.controller

import org.example.hbank.api.utility.TransactionStatus
import org.example.hbank.api.response.RequestResponse
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.service.*
import org.example.hbank.api.utility.Errors
import org.example.hbank.api.utility.asRequestResponse
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
import java.util.*

@RestController
@RequestMapping(path = ["api/v1/transaction/request"])
class RequestController(
    transactionManager: PlatformTransactionManager,
    private val userService: UserService,
    private val customerService: CustomerService,
    private val accountService: AccountService,
    private val requestService: RequestService
) {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    @PostMapping(
        path = ["create/username"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createRequestByUsername(
        @RequestParam(name = "username")
        username: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<RequestResponse> {

        val response = transactionTemplate.execute {

            val toAccount = getToAccount()
            val fromAccount = getFromAccountByUsername(username = username)

            val transaction = createRequest(
                toAccount = toAccount,
                fromAccount = fromAccount,
                amount = amount
            )

            transaction.asRequestResponse()
        }

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["create/email"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createRequestByEmail(
        @RequestParam(name = "email")
        email: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<RequestResponse> {

        val response = transactionTemplate.execute {

            val toAccount = getToAccount()
            val fromAccount = getFromAccountByEmail(email = email)

            val transaction = createRequest(
                toAccount = toAccount,
                fromAccount = fromAccount,
                amount = amount
            )

            transaction.asRequestResponse()
        }

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["create/phone_number"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createRequestByPhoneNumber(
        @RequestParam(name = "phone_number")
        phoneNumber: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<RequestResponse> {

        val response = transactionTemplate.execute {

            val toAccount = getToAccount()
            val fromAccount = getFromAccountByPhoneNumber(phoneNumber = phoneNumber)

            val transaction = createRequest(
                toAccount = toAccount,
                fromAccount = fromAccount,
                amount = amount
            )

            transaction.asRequestResponse()
        }

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["create/account_number"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createRequestAccountNumber(
        @RequestParam(name = "account_number")
        accountNumber: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<RequestResponse> {

        val response = transactionTemplate.execute {

            val toAccount = getToAccount()
            val fromAccount = getFromAccountByAccountNumber(accountNumber = accountNumber)

            val transaction = createRequest(
                toAccount = toAccount,
                fromAccount = fromAccount,
                amount = amount
            )

            transaction.asRequestResponse()
        }

        return ResponseEntity.ok(response)
    }

    private fun createRequest(toAccount: Account, fromAccount: Account, amount: Double): Transaction {

        if (requestService.reachDailyRequestLimit(account = toAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_DAILY_LIMIT)

        if (requestService.reachMonthlyRequestLimit(account = toAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_MONTHLY_LIMIT)

        if (requestService.reachYearlyRequestLimit(account = toAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_YEARLY_LIMIT)

        if (requestService.reachTransactionAmountLimit(account = toAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_AMOUNT_LIMIT)

        if (requestService.reachDailyRequestNumberLimit(account = toAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_DAILY_NUMBER_LIMIT)

        if (requestService.reachMonthlyRequestNumberLimit(account = toAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_MONTHLY_NUMBER_LIMIT)

        if (requestService.reachYearlyRequestNumberLimit(account = toAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_YEARLY_NUMBER_LIMIT)

        if (accountService.isSameAccount(account1 = toAccount, account2 = fromAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_SAME_ACCOUNT)

        if (accountService.isInactiveAccount(account = fromAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_SENDER_ACCOUNT_DEACTIVATED)

        if (accountService.isInactiveAccount(account = toAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_RECIPIENT_ACCOUNT_DEACTIVATED)

        val createdTransaction =
            requestService.createRequest(to = toAccount, from = fromAccount, amount = amount)

        return createdTransaction
    }

    @PostMapping(
        path = ["accept"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun acceptRequest(
        @RequestParam(name = "reference")
        reference: UUID,
    ): ResponseEntity<RequestResponse> {

        val response = transactionTemplate.execute {

            val transaction = getRequestByReference(reference = reference)

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            if (!requestService.isRequestRecipient(transaction = transaction, user = user))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_NOT_RECIPIENT)

            if (transaction.status == TransactionStatus.UNKNOWN)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)

            if (transaction.status == TransactionStatus.ACCEPTED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_ACCEPTED)

            if (transaction.status == TransactionStatus.DECLINED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_DECLINED)

            if (transaction.status == TransactionStatus.COMPLETED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_COMPLETED)

            if (transaction.status == TransactionStatus.CANCELED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_CANCELED)

            val acceptedTransaction = requestService.acceptTransaction(transaction = transaction)

            accountService.exchangeMoney(transaction = acceptedTransaction)

            val completedTransaction = requestService.completeTransaction(transaction = acceptedTransaction)

            requestService.asRequestResponse(transaction = completedTransaction)
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["decline"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun declineRequest(
        @RequestParam(name = "reference")
        reference: UUID
    ): ResponseEntity<RequestResponse> {

        val response = transactionTemplate.execute {

            val transaction = getRequestByReference(reference = reference)

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            if (!requestService.isRequestRecipient(transaction = transaction, user = user))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_NOT_RECIPIENT)

            if (transaction.status == TransactionStatus.UNKNOWN)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)

            if (transaction.status == TransactionStatus.ACCEPTED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_ACCEPTED)

            if (transaction.status == TransactionStatus.DECLINED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_DECLINED)

            if (transaction.status == TransactionStatus.COMPLETED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_COMPLETED)

            if (transaction.status == TransactionStatus.CANCELED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_CANCELED)

            val declinedTransaction = requestService.declineTransaction(transaction = transaction)

            requestService.asRequestResponse(transaction = declinedTransaction)
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["cancel"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun cancelRequest(
        @RequestParam(name = "reference")
        reference: UUID
    ): ResponseEntity<RequestResponse> {

        val response = transactionTemplate.execute {

            val transaction = getRequestByReference(reference = reference)

            val user = userService.getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

            if (!requestService.isRequestSender(transaction = transaction, user = user))
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_NOT_SENDER)

            if (transaction.status == TransactionStatus.UNKNOWN)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST)

            if (transaction.status == TransactionStatus.ACCEPTED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_ACCEPTED)

            if (transaction.status == TransactionStatus.DECLINED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_DECLINED)

            if (transaction.status == TransactionStatus.COMPLETED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_COMPLETED)

            if (transaction.status == TransactionStatus.CANCELED)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_ALREADY_CANCELED)

            val canceledTransaction = requestService.cancelTransaction(transaction = transaction)

            requestService.asRequestResponse(transaction = canceledTransaction)
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    private fun getRequestByReference(reference: UUID): Transaction {

        val transaction = requestService.getTransactionByReference(reference = reference)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_NOT_FOUND)

        if (!requestService.isRequest(transaction = transaction))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.REQUEST_NOT_REQUEST)

        return transaction
    }

    private fun getToAccount(): Account {

        val user = userService.getAuthenticatedUser()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_RECIPIENT_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_RECIPIENT_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_RECIPIENT_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getFromAccountByUsername(username: String): Account {

        val user = userService.getUserByUsername(username = username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getFromAccountByEmail(email: String): Account {

        val user = userService.getUserByEmail(email = email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getFromAccountByPhoneNumber(phoneNumber: String): Account {

        val user = userService.getUserByPhoneNumber(phoneNumber = phoneNumber)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getFromAccountByAccountNumber(accountNumber: String): Account =
        accountService.getAccountByNumber(number = accountNumber)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.REQUEST_SENDER_ACCOUNT_NOT_FOUND)

}


