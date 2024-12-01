package ai.tech.core.misc.type.serialization.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveDoubleSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Double,
    deserializer: (Double) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.DOUBLE,
    { encoder, value ->
        encoder.encodeDouble(serializer(value))
    },
    {
        deserializer(it.decodeDouble())
    },
)
