package org.example.hbank.api.utility

import com.waseetpay.api.response.*
import org.example.hbank.api.response.AccountResponse
import org.example.hbank.api.response.PersonalAccountResponse
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
import org.example.hbank.api.model.*
import org.example.hbank.api.response.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

fun Customer.asResponse() = CustomerResponse(
    username = user.username,
    email = user.email,
    phoneNumber = user.phoneNumber,
    name = getCustomerFullName(),
    birthdate = birthdate?.toKotlinLocalDate(),
    modified = modified.toKotlinInstant(),
    verified = verified == true,
    address = address?.asResponse(),
    avatar = avatar?.asCustomerAvatarUrl()
)

fun Address.asResponse() = AddressResponse(
    street = street,
    municipality = municipality,
    province = province,
    zip = zip
)

fun Account.asPersonalAccountResponse(token: String) = PersonalAccountResponse(
    name = name,
    number = number,
    balance = balance,
    status = status,
    limit = limit,
    modified = modified.toKotlinInstant(),
    token = token
)

fun Account.asResponse(): AccountResponse = AccountResponse(
    name = name,
    number = number,
    avatar = customer.avatar?.asCustomerAvatarUrl()
)

fun Transaction.asResponse() = TransactionResponse(
    reference = reference.asString(),
    created = created.toKotlinInstant(),
    modified = modified.toKotlinInstant(),
    amount = amount,
    fees = fees,
    type = type,
    status = status,
    from = from.asResponse(),
    to = to.asResponse()
)

fun Transaction.asTransferResponse(): TransferResponse = TransferResponse(
    reference = reference.asString(),
    created = created.toKotlinInstant(),
    amount = amount,
    status = status.asTransferStatus(),
    beneficiary = to.asResponse()
)

fun Transaction.asRequestResponse(): RequestResponse = RequestResponse(
    reference = reference.asString(),
    created = created.toKotlinInstant(),
    amount = amount,
    status = status.asRequestStatus(),
    recipient = from.asResponse()
)

private fun Customer.getCustomerFullName(): String =
    if (firstname.isNotEmpty() && lastname.isNotEmpty()) "$firstname $lastname"
    else user.email

private fun File.asCustomerAvatarUrl(): String = ServletUriComponentsBuilder
    .fromCurrentContextPath()
    .path("/api/v1/customer/avatar")
    .path(id.toString())
    .toUriString()
