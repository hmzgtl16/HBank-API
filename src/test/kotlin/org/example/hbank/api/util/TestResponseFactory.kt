package org.example.hbank.api.util

import com.waseetpay.api.response.AddressResponse
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.Transaction
import org.example.hbank.api.response.AccountResponse
import org.example.hbank.api.response.CustomerResponse
import org.example.hbank.api.response.PersonalAccountResponse
import org.example.hbank.api.response.TransactionResponse
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

object TestResponseFactory {

    fun toCustomerResponse(customer: Customer) = CustomerResponse(
        firstname = customer.firstname,
        lastname = customer.lastname,
        birthdate = customer.birthdate?.toKotlinLocalDate(),
        username = customer.user.username,
        email = customer.user.email,
        phoneNumber = customer.user.phoneNumber,
        address = customer.address?.let {
            AddressResponse(
                street = it.street,
                municipality = it.municipality,
                province = it.province,
                zip = it.province
            )
        },
        avatar = customer.avatar?.let {
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/customer/avatar")
                .path(it.id.toString())
                .toUriString()
        },
        modifiedAt = customer.modifiedAt.toKotlinInstant(),
        verified = customer.verified
    )

    fun toPersonalAccountResponse(account: Account) = PersonalAccountResponse(
        name = account.name,
        number = account.number,
        balance = account.balance,
        status = account.status,
        limit = account.limit,
        token = account.tokens.firstOrNull()?.token?.value ?: "456456873767890956789",
        modifiedAt = account.modifiedAt.toKotlinInstant()
    )

    fun toTransactionResponse(transaction: Transaction): TransactionResponse = TransactionResponse(
        reference = transaction.reference,
        amount = transaction.amount,
        fees = transaction.fees,
        status = transaction.status,
        type = transaction.type,
        from = toAccountResponse(account = transaction.from),
        to = toAccountResponse(account = transaction.to),
        createdAt = transaction.createdAt.toKotlinInstant(),
        modifiedAt = transaction.modifiedAt.toKotlinInstant()
    )

    fun toAccountResponse(account: Account): AccountResponse = AccountResponse(
        name = account.name,
        number = account.number,
        avatar = null
    )
}