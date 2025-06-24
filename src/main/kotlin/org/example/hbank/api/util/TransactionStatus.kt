package org.example.hbank.api.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TransactionStatusSerializer::class)
enum class TransactionStatus {

    CREATED,
    ACCEPTED,
    DECLINED,
    COMPLETED,
    CANCELED,
    UNKNOWN
}

object TransactionStatusSerializer : KSerializer<TransactionStatus> {
    override fun deserialize(decoder: Decoder): TransactionStatus =
        decoder.decodeInt().toTransactionStatus()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "transaction_status",
            kind = PrimitiveKind.INT,
        )

    override fun serialize(encoder: Encoder, value: TransactionStatus) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.toTransactionStatus(): TransactionStatus =
    TransactionStatus.entries.firstOrNull { this == it.ordinal } ?: TransactionStatus.UNKNOWN

fun TransactionStatus.asTransferStatus() = when (this) {
    TransactionStatus.CREATED -> TransferStatus.CREATED
    TransactionStatus.COMPLETED -> TransferStatus.COMPLETED
    else -> TransferStatus.UNKNOWN
}

fun TransactionStatus.asRequestStatus() = when (this) {
    TransactionStatus.CREATED -> RequestStatus.CREATED
    TransactionStatus.ACCEPTED -> RequestStatus.ACCEPTED
    TransactionStatus.DECLINED -> RequestStatus.DECLINED
    TransactionStatus.COMPLETED -> RequestStatus.COMPLETED
    TransactionStatus.CANCELED -> RequestStatus.CANCELED
    else -> RequestStatus.UNKNOWN
}