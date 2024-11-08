package ai.tech.core.misc.plugin.forwardedheaders.mode.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ForwardedHeadersConfig(
    override val useFirst: Boolean? = null,
    override val useLast: Boolean? = null,
    override val skipLastProxies: Int? = null,
    override val skipKnownProxies: List<String>? = null,
    override val enable: Boolean = true,
) : ForwardedHeaderConfig0, EnabledConfig
