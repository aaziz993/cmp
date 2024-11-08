package ai.tech.core.misc.plugin.compression.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CompressionConfig(
    val gzip: CompressionEncoderConfig? = null,
    val deflate: CompressionEncoderConfig? = null,
    val identity: CompressionEncoderConfig? = null,
    override val enable: Boolean = true,
) : EnabledConfig
