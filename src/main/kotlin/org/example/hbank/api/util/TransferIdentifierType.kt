package org.example.hbank.api.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Serializable(with = TransferIdentifierTypeSerializer::class)
enum class TransferIdentifierType {
    USERNAME,
    EMAIL,
    PHONE_NUMBER,
    ACCOUNT_NUMBER,
    ACCOUNT_TOKEN
}

object TransferIdentifierTypeSerializer : KSerializer<TransferIdentifierType> {
    override fun deserialize(decoder: Decoder): TransferIdentifierType =
        decoder.decodeInt().asTransferIdentifierType()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "transfer_identifier_type",
            kind = PrimitiveKind.INT
        )

    override fun serialize(encoder: Encoder, value: TransferIdentifierType) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.asTransferIdentifierType(): TransferIdentifierType =
    TransferIdentifierType.entries.firstOrNull { this == it.ordinal }
        ?: throw AccountInvalidIdentifierException()