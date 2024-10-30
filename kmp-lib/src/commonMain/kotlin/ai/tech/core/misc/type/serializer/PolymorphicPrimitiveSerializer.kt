package ai.tech.core.misc.type.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@OptIn(ExperimentalSerializationApi::class)
public class PolymorphicPrimitiveSerializer<T>(private val serializer: KSerializer<T>) : KSerializer<T> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(serializer.descriptor.serialName)
    {
        element("value", serializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: T): Unit =
        encoder.encodeStructure(descriptor) { encodeSerializableElement(descriptor, 0, serializer, value) }

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeStructure(descriptor) {
            decodeElementIndex(descriptor)
            decodeSerializableElement(descriptor, 0, serializer)
        }
}