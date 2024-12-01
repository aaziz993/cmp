@file:OptIn(ExperimentalUuidApi::class)

package ai.tech.core.misc.type.serialization.serializer.primitive

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@OptIn(ExperimentalUuidApi::class)
public object UuidSerializer :
    PrimitiveStringSerializer<Uuid>(
        Uuid::class,
        Uuid::toString,
        Uuid.Companion::parse,
    )

public typealias UuidSerial = @Serializable(with = UuidSerializer::class) Uuid
