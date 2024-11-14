package ai.tech.core.misc.type.serializer.primitive;

import ai.tech.core.misc.type.multiple.decode
import ai.tech.core.misc.type.multiple.decodeBase64
import io.ktor.util.encodeBase64

public class Base64Serializer : PrimitiveStringSerializer<String>(
    String::class, { it.encodeBase64() },
    { it.decodeBase64().decode() },
)
