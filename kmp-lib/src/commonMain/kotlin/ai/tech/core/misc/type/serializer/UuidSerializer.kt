package ai.tech.core.misc.type.serializer

import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.Serializable

public object UuidSerializer :
    PrimitiveStringSerializer<Uuid>(
        Uuid::class,
        Uuid::toString,
        ::uuidFrom,
    )

public typealias UuidSerial = @Serializable(with = UuidSerializer::class) Uuid
