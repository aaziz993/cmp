package ai.tech.core.misc.type.serializer

import io.ktor.http.*
import kotlinx.serialization.Serializable

public object HttpStatusCodeSerializer :
    PrimitiveSerializer<HttpStatusCode>(
        HttpStatusCode::class,
        { HttpStatusCode.fromValue(it.toInt()) },
        { it.value.toString() }
    )

public typealias HttpStatusCodeSerial = @Serializable(with = HttpStatusCodeSerializer::class) HttpStatusCode