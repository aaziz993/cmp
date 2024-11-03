package ai.tech.core.misc.plugin.forwardedheaders.mode.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ForwardedHeadersConfig(
    val useFirst: Boolean? = null,
    val useLast: Boolean? = null,
    val skipLastProxies: Int? = null,
    val skipKnownProxies: List<String>? = null,
    val xForwardedHostHeaders: List<String>? = null,
    val xForwardedProtoHeaders: List<String>? = null,
    val xForwardedForHeaders: List<String>? = null,
    val xForwardedHttpsFlagHeaders: List<String>? = null,
    val xForwardedPortHeaders: List<String>? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
