package ai.tech.core.misc.plugin.compression.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.type.serializer.http.ContentTypeSerial
import kotlinx.serialization.Serializable

@Serializable
public data class CompressionEncoderConfig(
    val priority: Double? = null,
    val minimumSize: Long? = null,
    val matchContentType: List<ContentTypeSerial>? = null,
    val excludeContentType: List<ContentTypeSerial>? = null,
    override val enabled: Boolean = true,
) : EnabledConfig


