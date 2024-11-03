package ai.tech.core.misc.type.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveFloatSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Float,
    deserializer: (Float) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.FLOAT,
    { encoder, value ->
        encoder.encodeFloat(serializer(value))
    },
    {
        deserializer(it.decodeFloat())
    },
)
