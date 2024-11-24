package ai.tech.core.misc.plugin.routing.model.config

import ai.tech.core.data.model.Compression
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.cachingheaders.model.config.CacheControlConfig
import ai.tech.core.misc.type.serializer.http.ContentTypeSerial
import kotlinx.serialization.Serializable

@Serializable
public data class StaticContentConfig(
    val remotePath: String,
    val pathName: String,
    val index: String = "index.html",
    val defaultPath: String? = null,
    val enableAutoHeadResponse: Boolean? = null,
    val preCompressed: Set<Compression>? = null,
    val contentType: Map<String, ContentTypeSerial>? = null,
    val cacheControl: Map<String, List<CacheControlConfig>>? = null,
    val excludePaths: Set<String>? = null,
    val extensions: Set<String>? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
