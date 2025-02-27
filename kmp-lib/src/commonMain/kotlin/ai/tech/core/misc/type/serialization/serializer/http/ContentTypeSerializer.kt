package ai.tech.core.misc.type.serialization.serializer.http

import ai.tech.core.misc.type.serialization.serializer.primitive.PrimitiveStringSerializer
import io.ktor.http.*
import kotlinx.serialization.Serializable

public object ContentTypeSerializer :
    PrimitiveStringSerializer<ContentType>(
        ContentType::class,
        ContentType::toString,
        ContentType::parse,
    )

public typealias ContentTypeSerial = @Serializable(with = ContentTypeSerializer::class) ContentType
