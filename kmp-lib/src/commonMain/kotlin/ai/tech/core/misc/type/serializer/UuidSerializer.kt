package ai.tech.core.misc.type.serializer

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.Serializable

public object UuidSerializer :
    PrimitiveSerializer<Uuid>(
        Uuid::class,
        { uuidFrom(it) },
        { it.toString() }
    )

public typealias UuidSerial = @Serializable(with = UuidSerializer::class) Uuid