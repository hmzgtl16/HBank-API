package org.example.hbank.api.utility

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TransactionTypeSerializer::class)
enum class TransactionType {

    TRANSFER,
    REQUEST,
    UNKNOWN
}

object TransactionTypeSerializer : KSerializer<TransactionType> {
    override fun deserialize(decoder: Decoder): TransactionType =
        decoder.decodeInt().toTransactionType()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "transaction_type",
            kind = PrimitiveKind.INT,
        )

    override fun serialize(encoder: Encoder, value: TransactionType) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.toTransactionType(): TransactionType =
    TransactionType.entries.firstOrNull { this == it.ordinal } ?: TransactionType.UNKNOWN

