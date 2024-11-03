package ai.tech.core.misc.plugin.cachingheaders.model.config

import ai.tech.core.misc.type.serializer.ContentTypeSerial
import kotlinx.serialization.Serializable

@Serializable
public data class CacheContentTypeOptionConfig(
    val contentType: ContentTypeSerial,
    val cacheControl: CacheControlConfig,
)
