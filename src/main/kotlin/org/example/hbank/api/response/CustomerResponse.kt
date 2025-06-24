package org.example.hbank.api.response

import com.waseetpay.api.response.AddressResponse
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerResponse(
    @SerialName("firstname") val firstname: String?,
    @SerialName("lastname") val lastname: String?,
    @SerialName("birthdate") val birthdate: LocalDate?,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("phone_number") val phoneNumber: String?,
    @SerialName("address") val address: AddressResponse?,
    @SerialName("avatar") val avatar: String?,
    @SerialName("modified_at") val modifiedAt: Instant,
    @SerialName("verified") val verified: Boolean
)

