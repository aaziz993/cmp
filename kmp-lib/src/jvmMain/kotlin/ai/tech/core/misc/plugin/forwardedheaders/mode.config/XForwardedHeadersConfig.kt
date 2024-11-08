package ai.tech.core.misc.plugin.forwardedheaders.mode.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class XForwardedHeadersConfig(
    val hostHeaders: List<String>? = null,
    val protoHeaders: List<String>? = null,
    val forHeaders: List<String>? = null,
    val httpsFlagHeaders: List<String>? = null,
    val portHeaders: List<String>? = null,
    override val useFirst: Boolean? = null,
    override val useLast: Boolean? = null,
    override val skipLastProxies: Int? = null,
    override val skipKnownProxies: List<String>? = null,
    override val enable: Boolean = true,
) : ForwardedHeaderConfig0, EnabledConfig
