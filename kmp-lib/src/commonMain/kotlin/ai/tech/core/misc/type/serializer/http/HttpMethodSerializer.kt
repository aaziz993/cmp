package ai.tech.core.misc.type.serializer.http

import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import io.ktor.http.*
import kotlinx.serialization.Serializable

public object HttpMethodSerializer :
    PrimitiveStringSerializer<HttpMethod>(
        HttpMethod::class,
        HttpMethod::value,
        HttpMethod::parse,
    )

public typealias HttpMethodSerial = @Serializable(with = HttpMethodSerializer::class) HttpMethod
