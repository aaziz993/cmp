@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package ai.tech.core.misc.type.serializer

import kotlin.reflect.KClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.serializer

public class PolymorphicSerializer<T : Any>(public val serializer: KSerializer<T>) : KSerializer<T> {

    public constructor(kClass: KClass<T>) : this(kClass.serializer())

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

public fun <T : Any> PolymorphicModuleBuilder<T>.subclass(kClass: KClass<T>) {
    subclass(kClass, PolymorphicSerializer(kClass.serializer()))
}

@Suppress("UNCHECKED_CAST")
public fun <T : Any> PolymorphicModuleBuilder<T>.subclass(serializer: KSerializer<T>) = subclass(serializer.descriptor.capturedKClass as KClass<T>, PolymorphicSerializer(serializer))
