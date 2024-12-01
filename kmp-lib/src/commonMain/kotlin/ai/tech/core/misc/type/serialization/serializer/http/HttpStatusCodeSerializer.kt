package ai.tech.core.misc.type.serialization.serializer.http

import ai.tech.core.misc.type.serialization.serializer.primitive.PrimitiveIntSerializer
import io.ktor.http.*
import kotlinx.serialization.Serializable

public object HttpStatusCodeSerializer :
    PrimitiveIntSerializer<HttpStatusCode>(
        HttpStatusCode::class,
        HttpStatusCode::value,
        HttpStatusCode::fromValue,
    )

public typealias HttpStatusCodeSerial = @Serializable(with = HttpStatusCodeSerializer::class) HttpStatusCode
