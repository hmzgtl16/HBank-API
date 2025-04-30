package com.waseetpay.api.response

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Past
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerResponse(
    @SerialName(value = "username")
    val username: String,

    @SerialName(value = "name")
    val name: String,

    @Past
    @SerialName(value = "birthdate")
    val birthdate: LocalDate?,

    @SerialName(value = "address")
    val address: AddressResponse?,

    @SerialName(value = "email")
    @Email
    val email: String,

    @SerialName(value = "phone_number")
    val phoneNumber: String?,

    @SerialName(value = "modified")
    val modified: Instant,

    @SerialName(value = "verified")
    val verified: Boolean,

    @SerialName(value = "avatar")
    val avatar: String?
)

