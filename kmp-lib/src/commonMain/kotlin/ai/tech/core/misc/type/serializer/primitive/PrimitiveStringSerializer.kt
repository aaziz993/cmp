package ai.tech.core.misc.type.serializer.primitive

import kotlin.reflect.KClass
import kotlinx.serialization.descriptors.PrimitiveKind

public abstract class PrimitiveStringSerializer<T : Any>(
    kClass: KClass<T>,
    serializer: (T) -> String,
    deserializer: (String) -> T,
) : PrimitiveSerializer<T>(kClass,
    PrimitiveKind.STRING,
    { encoder, value ->
        encoder.encodeString(serializer(value))
    }, {
        deserializer(it.decodeString())
    })
