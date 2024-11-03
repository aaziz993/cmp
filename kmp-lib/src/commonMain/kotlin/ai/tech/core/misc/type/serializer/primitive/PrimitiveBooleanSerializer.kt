package ai.tech.core.misc.type.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveBooleanSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> Boolean,
    deserializer: (Boolean) -> T,
) : PrimitiveSerializer<T>(
    kClass,
    PrimitiveKind.INT,
    { encoder, value ->
        encoder.encodeBoolean(serializer(value))
    },
    {
        deserializer(it.decodeBoolean())
    },
)
