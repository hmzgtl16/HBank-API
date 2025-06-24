package org.example.hbank.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCustomerAddressRequest(
    @SerialName("municipality") val municipality: String,
    @SerialName("province") val province: String,
    @SerialName("street") val street: String,
    @SerialName("zip") val zip: String
)
