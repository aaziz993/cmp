package ai.tech.core.misc.type.serialization.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveCharSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Char,
    deserializer: (Char) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.CHAR,
    { encoder, value ->
        encoder.encodeChar(serializer(value))
    },
    {
        deserializer(it.decodeChar())
    },
)
