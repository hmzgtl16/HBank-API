package org.example.hbank.api.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

enum class RequestIdentifierType {
    USERNAME,
    EMAIL,
    PHONE_NUMBER,
    ACCOUNT_NUMBER,
}

object RequestIdentifierTypeSerializer : KSerializer<RequestIdentifierType> {
    override fun deserialize(decoder: Decoder): RequestIdentifierType =
        decoder.decodeInt().asRequestIdentifierType()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "transfer_identifier_type",
            kind = PrimitiveKind.INT
        )

    override fun serialize(encoder: Encoder, value: RequestIdentifierType) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.asRequestIdentifierType(): RequestIdentifierType =
    RequestIdentifierType.entries.firstOrNull { this == it.ordinal }
        ?: throw AccountInvalidIdentifierException()
