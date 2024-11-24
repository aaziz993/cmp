package ai.tech.core.misc.plugin.hsts.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class HSTSHostConfig(
    val preload: Boolean? = null,
    val includeSubDomains: Boolean? = null,
    val maxAgeInSeconds: Long? = null,
    val customDirectives: Map<String, String?>? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
