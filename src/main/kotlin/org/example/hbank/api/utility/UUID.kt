package org.example.hbank.api.utility

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

object UUIDSerializer : KSerializer<UUID> {
    override fun deserialize(decoder: Decoder): UUID =
        decoder.decodeString().asUUID()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "uuid",
            kind = PrimitiveKind.STRING,
        )

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value = value.asString())
    }
}

fun String.asUUID(): UUID = UUID.fromString(this)

fun UUID.asString(): String = toString()