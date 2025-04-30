package org.example.hbank.api.controller

import org.example.hbank.api.model.Account
import org.example.hbank.api.response.TransferResponse
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.service.*
import org.example.hbank.api.utility.Errors
import org.example.hbank.api.utility.asTransferResponse
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

@RestController
@RequestMapping(path = ["api/v1/transaction/transfer"])
class TransferController(
    transactionManager: PlatformTransactionManager,
    private val userService: UserService,
    private val accountService: AccountService,
    private val transferService: TransferService,
    private val tokenService: TokenService,
    private val customerService: CustomerService
) {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    @PostMapping(
        path = ["create/username"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTransferByUsername(
        @RequestParam(name = "username")
        username: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<TransferResponse> {

        val response = transactionTemplate.execute {

            val fromAccount = getFromAccount()
            val toAccount = getToAccountByUsername(username = username)

            val transaction = createTransfer(
                fromAccount = fromAccount,
                toAccount = toAccount,
                amount = amount
            )

            transaction.asTransferResponse()
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["create/email"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTransferByEmail(
        @RequestParam(name = "email")
        email: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<TransferResponse> {

        val response = transactionTemplate.execute {

            val fromAccount = getFromAccount()
            val toAccount = getToAccountByEmail(email = email)

            val transaction = createTransfer(
                fromAccount = fromAccount,
                toAccount = toAccount,
                amount = amount
            )

            transaction.asTransferResponse()
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["create/phone_number"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTransferByPhoneNumber(
        @RequestParam(name = "phone_number")
        phoneNumber: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<TransferResponse> {

        val response = transactionTemplate.execute {

            val fromAccount = getFromAccount()
            val toAccount = getToAccountByPhoneNumber(phoneNumber = phoneNumber)

            val transaction = createTransfer(
                fromAccount = fromAccount,
                toAccount = toAccount,
                amount = amount
            )

            transaction.asTransferResponse()
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["create/account_number"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTransferByAccountNumber(
        @RequestParam(name = "account_number")
        accountNumber: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<TransferResponse> {

        val response = transactionTemplate.execute {

            val fromAccount = getFromAccount()
            val toAccount = getToAccountByAccountNumber(accountNumber = accountNumber)

            val transaction = createTransfer(
                fromAccount = fromAccount,
                toAccount = toAccount,
                amount = amount
            )

            transaction.asTransferResponse()
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    @PostMapping(
        path = ["create/account_token"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTransferByAccountToken(
        @RequestParam(name = "account_token")
        accountToken: String,
        @RequestParam(name = "amount")
        amount: Double
    ): ResponseEntity<TransferResponse> {

        val response = transactionTemplate.execute {

            val fromAccount = getFromAccount()
            val toAccount = getToAccountByAccountToken(accountToken = accountToken)

            val transaction = createTransfer(
                fromAccount = fromAccount,
                toAccount = toAccount,
                amount = amount
            )

            transaction.asTransferResponse()
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(response)
    }

    /*
        @PostMapping(
            path = ["verify"],
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE]
        )
        fun verifyTransfer(
            @RequestParam(name = "reference") reference: UUID
        ): ResponseEntity<TransferResponse> {

            val user = userService.getCurrentUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)
            val transaction = transferService.getTransactionByReference(reference = reference)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_NOT_FOUND)

            when {
                transferService.isTransfer(transaction = transaction).not() ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_TRANSFER)

                transferService.isTransferPayer(transaction = transaction, user = user).not() ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_PAYER)
            }

            return when (transaction.status) {
                TransactionStatus.CREATED -> {
                    tokenService.createTransactionToken(transaction = transaction).also { token ->
                        mailService.sendVerifyTransferEmail(
                            username = transaction.from.customer.user.username,
                            email = transaction.from.customer.user.email,
                            token = token.token.value
                        )
                    }
                    ResponseEntity.ok(transaction.asTransferResponse())
                }

                TransactionStatus.VERIFIED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_VERIFIED)

                TransactionStatus.CANCELED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_CANCELED)

                TransactionStatus.COMPLETED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_COMPLETED)

                else ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_TRANSFER)
            }
        }

        @PostMapping(
            path = ["confirm"],
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE]
        )
        fun confirmTransfer(
            @RequestParam(name = "token") token: String
        ): ResponseEntity<TransferResponse> {
            val user = userService.getCurrentUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)
            val transactionToken = tokenService.getTransactionToken(value = token)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TOKEN_NOT_FOUND)
            when {
                transferService.isTransfer(transaction = transactionToken.transaction).not() ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_TRANSFER)

                transferService.isTransferPayer(transaction = transactionToken.transaction, user = user).not() ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_PAYER)

                Validator.isValidToken(token = token).not() ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TOKEN_INVALID)

                tokenService.isTransactionTokenExpired(token = transactionToken) ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TOKEN_EXPIRED)
            }
            return when (transactionToken.transaction.status) {
                TransactionStatus.CREATED -> {
                    val confirmedTransaction =
                        transferService.confirmTransaction(transaction = transactionToken.transaction)

                    val transactionWithFees =
                        transferService.addTransactionFees(transaction = confirmedTransaction)
                    accountService.exchangeMoney(transaction = transactionWithFees)
                    val completedTransaction = transferService.completeTransaction(transaction = transactionWithFees)
                    tokenService
                        .getMessageToken(completedTransaction.to.customer.user)
                        ?.also {
                            messageService.sendReceivedTransferMessageToUser(
                                token = it.token.value,
                                beneficiary = completedTransaction.to.name,
                                amount = completedTransaction.amount,
                                reference = completedTransaction.reference.toString()
                            )
                        }
                    ResponseEntity.ok(completedTransaction.asTransferResponse())
                }

                TransactionStatus.VERIFIED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_VERIFIED)

                TransactionStatus.CANCELED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_CANCELED)

                TransactionStatus.COMPLETED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_COMPLETED)

                else ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_TRANSFER)
            }
        }

        @PostMapping(
            path = ["cancel"],
            produces = [MediaType.APPLICATION_JSON_VALUE]
        )
        fun cancelTransfer(
            @RequestParam(name = "reference") reference: UUID
        ): ResponseEntity<TransferResponse> {

            val user = userService.getCurrentUser()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)
            val transaction = transferService.getTransactionByReference(reference = reference)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_NOT_FOUND)
            when {
                transferService.isTransfer(transaction = transaction).not() ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_TRANSFER)

                transferService.isTransferPayer(transaction = transaction, user = user).not() ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_PAYER)
            }

            return when (transaction.status) {
                TransactionStatus.CREATED, TransactionStatus.VERIFIED -> {
                    val canceledTransaction = transferService.cancelTransaction(transaction = transaction)
                    ResponseEntity.ok(canceledTransaction.asTransferResponse())
                }

                TransactionStatus.CANCELED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_CANCELED)

                TransactionStatus.COMPLETED ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_ALREADY_COMPLETED)

                else ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_NOT_TRANSFER)
            }
        }*/

    private fun createTransfer(fromAccount: Account, toAccount: Account, amount: Double): Transaction {

        if (transferService.reachDailyTransferLimit(account = fromAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_DAILY_LIMIT)

        if (transferService.reachMonthlyTransferLimit(account = fromAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_MONTHLY_LIMIT)

        if (transferService.reachYearlyTransferLimit(account = fromAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_YEARLY_LIMIT)

        if (transferService.reachTransactionAmountLimit(account = fromAccount, amount = amount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_AMOUNT_LIMIT)

        if (transferService.reachDailyTransferNumberLimit(account = fromAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_DAILY_NUMBER_LIMIT)

        if (transferService.reachMonthlyTransferNumberLimit(account = fromAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_MONTHLY_NUMBER_LIMIT)

        if (transferService.reachYearlyTransferNumberLimit(account = fromAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_YEARLY_NUMBER_LIMIT)

        if (accountService.isSameAccount(account1 = fromAccount, account2 = toAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_SAME_ACCOUNT)

        if (accountService.isInactiveAccount(account = fromAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_PAYER_DEACTIVATED_ACCOUNT)

        if (accountService.isInactiveAccount(account = toAccount))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_RECIPIENT_DEACTIVATED_ACCOUNT)

        if (accountService.isBalanceSufficient(account = fromAccount, amount = amount).not())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.TRANSFER_INSUFFICIENT_BALANCE)

        val createdTransaction =
            transferService.createTransfer(from = fromAccount, to = toAccount, amount = amount)

        val transactionWithFees =
            transferService.addTransactionFees(transaction = createdTransaction)

        accountService.exchangeMoney(transaction = transactionWithFees)

        val completedTransaction =
            transferService.completeTransaction(transaction = transactionWithFees)

        return completedTransaction
    }

    private fun getFromAccount(): Account {

        val user = userService.getAuthenticatedUser()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_PAYER_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_PAYER_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_PAYER_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getToAccountByUsername(username: String): Account {

        val user = userService.getUserByUsername(username = username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getToAccountByEmail(email: String): Account {

        val user = userService.getUserByEmail(email = email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getToAccountByPhoneNumber(phoneNumber: String): Account {

        val user = userService.getUserByPhoneNumber(phoneNumber = phoneNumber)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_USER_NOT_FOUND)

        val customer = customerService.getCustomerByUser(user = user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_CUSTOMER_NOT_FOUND)

        val account = accountService.getCustomerPersonalAccount(customer = customer)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_ACCOUNT_NOT_FOUND)

        return account
    }

    private fun getToAccountByAccountNumber(accountNumber: String): Account =
        accountService.getAccountByNumber(number = accountNumber)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_ACCOUNT_NOT_FOUND)

    private fun getToAccountByAccountToken(accountToken: String): Account =
        tokenService.getAccountToken(value = accountToken)?.account
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.TRANSFER_RECIPIENT_ACCOUNT_NOT_FOUND)
}

