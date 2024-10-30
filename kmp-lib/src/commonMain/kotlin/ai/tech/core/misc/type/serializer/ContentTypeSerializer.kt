package ai.tech.core.misc.type.serializer

import io.ktor.http.*
import kotlinx.serialization.Serializable

public object ContentTypeSerializer :
    PrimitiveSerializer<ContentType>(
        ContentType::class,
        { ContentType.parse(it) },
        { it.toString() }
    )


public typealias ContentTypeSerial = @Serializable(with = ContentTypeSerializer::class) ContentType