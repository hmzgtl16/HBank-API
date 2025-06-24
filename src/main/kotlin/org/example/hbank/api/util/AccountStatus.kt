package org.example.hbank.api.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AccountStatusSerializer::class)
enum class AccountStatus {

    ACTIVATED,
    DEACTIVATED,
    UNKNOWN
}

object AccountStatusSerializer : KSerializer<AccountStatus> {
    override fun deserialize(decoder: Decoder): AccountStatus =
        decoder.decodeInt().asAccountStatus()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "account status",
            kind = PrimitiveKind.INT
        )

    override fun serialize(encoder: Encoder, value: AccountStatus) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.asAccountStatus(): AccountStatus =
    AccountStatus.entries.firstOrNull { this == it.ordinal } ?: AccountStatus.UNKNOWN
