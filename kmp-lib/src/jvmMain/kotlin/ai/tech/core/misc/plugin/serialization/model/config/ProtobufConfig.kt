package ai.tech.core.misc.plugin.serialization.model.config

import ai.tech.core.misc.type.serializer.http.ContentTypeSerial
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
public data class ProtobufConfig(
    val encodeDefaults: Boolean? = null,
    override val contentType: ContentTypeSerial = ContentType.Application.ProtoBuf,
    override val enable: Boolean? = null,
) : SerializationFormatConfig
