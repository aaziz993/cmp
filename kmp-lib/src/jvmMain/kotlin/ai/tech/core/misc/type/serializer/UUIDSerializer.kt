package ai.tech.core.misc.type.serializer

import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

public class UUIDSerializer :
    PrimitiveStringSerializer<UUID>(
        UUID::class,
        UUID::toString,
        UUID::fromString,
    )

public typealias UUIDSerial = @Serializable(with = UUIDSerializer::class) UUID
