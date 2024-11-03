package ai.tech.core.misc.type.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveShortSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Short,
    deserializer: (Short) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.INT,
    { encoder, value ->
        encoder.encodeShort(serializer(value))
    },
    {
        deserializer(it.decodeShort())
    },
)
