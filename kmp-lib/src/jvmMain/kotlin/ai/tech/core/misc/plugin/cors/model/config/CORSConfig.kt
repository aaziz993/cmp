package ai.tech.core.misc.plugin.cors.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.type.serializer.http.HttpMethodSerial
import kotlinx.serialization.Serializable

@Serializable
public data class CORSConfig(
    val hosts: MutableSet<CORSHostConfig>? = null,
    val headers: MutableSet<String>? = null,
    val methods: MutableSet<HttpMethodSerial>? = null,
    val exposedHeaders: MutableSet<String>? = null,
    var allowCredentials: Boolean? = null,
    val maxAgeInSeconds: Long? = null,
    val allowSameOrigin: Boolean? = null,
    val allowNonSimpleContentTypes: Boolean? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
