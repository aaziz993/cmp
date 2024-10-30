package ai.tech.core.misc.type.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

public abstract class PrimitiveSerializer<T : Any>(
    kClass: KClass<T>,
    public val parser: (String) -> T,
    public val serializer: (T) -> String = { it.toString() },
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            kClass.simpleName!!,
            PrimitiveKind.STRING,
        )

    override fun serialize(
        encoder: Encoder,
        value: T,
    ) {
        encoder.encodeString(serializer(value))
    }

    override fun deserialize(decoder: Decoder): T = parser(decoder.decodeString())
}