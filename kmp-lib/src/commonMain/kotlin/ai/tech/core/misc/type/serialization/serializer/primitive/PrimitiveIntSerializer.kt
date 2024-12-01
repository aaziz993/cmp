package ai.tech.core.misc.type.serialization.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveIntSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Int,
    deserializer: (Int) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.INT,
    { encoder, value ->
        encoder.encodeInt(serializer(value))
    },
    {
        deserializer(it.decodeInt())
    },
)
