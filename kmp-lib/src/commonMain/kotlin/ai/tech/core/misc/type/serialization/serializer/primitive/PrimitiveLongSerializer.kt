package ai.tech.core.misc.type.serialization.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveLongSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Long,
    deserializer: (Long) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.LONG,
    { encoder, value ->
        encoder.encodeLong(serializer(value))
    },
    {
        deserializer(it.decodeLong())
    },
)
