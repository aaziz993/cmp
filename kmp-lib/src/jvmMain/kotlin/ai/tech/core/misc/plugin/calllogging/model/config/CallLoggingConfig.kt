package ai.tech.core.misc.plugin.calllogging.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.LogConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CallLoggingConfig(
    val logging: LogConfig? = null,
    val disableDefaultColors: Boolean? = null,
    val disableForStaticContent: Boolean? = null,
    override val enable: Boolean = true,
) : EnabledConfig
