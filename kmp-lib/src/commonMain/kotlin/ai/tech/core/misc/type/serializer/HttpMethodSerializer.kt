package ai.tech.core.misc.type.serializer

import io.ktor.http.*
import kotlinx.serialization.Serializable

public object HttpMethodSerializer :
    PrimitiveSerializer<HttpMethod>(
        HttpMethod::class,
        { HttpMethod.parse(it) },
        { it.value }
    )

public typealias HttpMethodSerial = @Serializable(with = HttpMethodSerializer::class) HttpMethod