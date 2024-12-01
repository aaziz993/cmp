package ai.tech.core.misc.type.serialization.serializer.http

import ai.tech.core.misc.type.serialization.serializer.primitive.PrimitiveStringSerializer
import io.ktor.http.*
import kotlinx.serialization.Serializable

public object HttpMethodSerializer :
    PrimitiveStringSerializer<HttpMethod>(
        HttpMethod::class,
        HttpMethod::value,
        HttpMethod::parse,
    )

public typealias HttpMethodSerial = @Serializable(with = HttpMethodSerializer::class) HttpMethod
