package org.example.hbank.api.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RequestStatusSerializer::class)
enum class RequestStatus {
    CREATED,
    ACCEPTED,
    DECLINED,
    COMPLETED,
    CANCELED,

    UNKNOWN
}

private object RequestStatusSerializer : KSerializer<RequestStatus> {

    override fun deserialize(decoder: Decoder): RequestStatus =
        decoder.decodeInt().toRequestStatus()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "status",
            kind = PrimitiveKind.INT,
        )

    override fun serialize(encoder: Encoder, value: RequestStatus) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.toRequestStatus(): RequestStatus =
    RequestStatus.entries.firstOrNull { this == it.ordinal } ?: RequestStatus.UNKNOWN