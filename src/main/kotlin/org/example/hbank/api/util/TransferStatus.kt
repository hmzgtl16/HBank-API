package org.example.hbank.api.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TransferStatusSerializer::class)
enum class TransferStatus {
    CREATED,
    COMPLETED,

    UNKNOWN
}

object TransferStatusSerializer : KSerializer<TransferStatus> {
    override fun deserialize(decoder: Decoder): TransferStatus =
        decoder.decodeInt().toTransferStatus()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "transfer_status",
            kind = PrimitiveKind.INT,
        )

    override fun serialize(encoder: Encoder, value: TransferStatus) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.toTransferStatus(): TransferStatus =
    TransferStatus.entries.firstOrNull { this == it.ordinal } ?: TransferStatus.UNKNOWN

