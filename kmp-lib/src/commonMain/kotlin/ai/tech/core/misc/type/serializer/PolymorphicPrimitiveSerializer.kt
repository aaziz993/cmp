@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package ai.tech.core.misc.type.serializer

import kotlin.reflect.KClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer

public class PolymorphicPrimitiveSerializer<T : Any>(public val serializer: KSerializer<T>) : KSerializer<T> {

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

@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any> PolymorphicModuleBuilder<T>.primitive(serializer: KSerializer<T>) =
    subclass(PolymorphicPrimitiveSerializer(serializer))

@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any> PolymorphicModuleBuilder<T>.primitive(kClass: KClass<T>) =
    primitive(kClass.serializer())
