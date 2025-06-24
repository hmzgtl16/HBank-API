package org.example.hbank.api.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AccountTypeSerializer::class)
enum class AccountType {

    PERSONAL_ACCOUNT,
    BUSINESS_ACCOUNT,
    UNKNOWN
}

object AccountTypeSerializer : KSerializer<AccountType> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "account type",
            kind = PrimitiveKind.INT,
        )

    override fun deserialize(decoder: Decoder): AccountType =
        decoder.decodeInt().asAccountType()

    override fun serialize(encoder: Encoder, value: AccountType) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.asAccountType(): AccountType =
    AccountType.entries.firstOrNull { it.ordinal == this } ?: AccountType.UNKNOWN