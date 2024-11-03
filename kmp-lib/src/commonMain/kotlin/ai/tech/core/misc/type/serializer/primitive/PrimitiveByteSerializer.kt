package ai.tech.core.misc.type.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveByteSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Byte,
    deserializer: (Byte) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.BYTE,
    { encoder, value ->
        encoder.encodeByte(serializer(value))
    },
    {
        deserializer(it.decodeByte())
    },
)
