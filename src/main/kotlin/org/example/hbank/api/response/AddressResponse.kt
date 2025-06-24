package com.waseetpay.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(
    @SerialName(value = "street") val street: String?,
    @SerialName(value = "municipality") val municipality: String?,
    @SerialName(value = "province") val province: String?,
    @SerialName(value = "zip") val zip: String?
)
