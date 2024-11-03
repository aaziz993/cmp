package ai.tech.core.misc.type.serializer.http

import ai.tech.core.misc.type.serializer.primitive.PrimitiveIntSerializer
import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import io.ktor.http.*
import kotlinx.serialization.Serializable

public object HttpStatusCodeSerializer :
    PrimitiveIntSerializer<HttpStatusCode>(
        HttpStatusCode::class,
        HttpStatusCode::value,
        HttpStatusCode::fromValue,
    )

public typealias HttpStatusCodeSerial = @Serializable(with = HttpStatusCodeSerializer::class) HttpStatusCode
