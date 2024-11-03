package ai.tech.core.misc.plugin.shutdown.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ShutDownConfig(
    val shutDownUrl: String? = null,
    val exitCodeSupplier: Int? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
