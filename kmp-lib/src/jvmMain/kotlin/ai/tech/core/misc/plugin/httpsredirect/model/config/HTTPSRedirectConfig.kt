package ai.tech.core.misc.plugin.httpsredirect.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class HTTPSRedirectConfig(
    val sslPort: Int? = null,
    val permanentRedirect: Boolean? = null,
    val excludePrefix: String? = null,
    val excludeSuffix: String? = null,
    override val enable: Boolean = true,
) : EnabledConfig
