package ai.tech.core.misc.plugin.callid.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CallIdVerifyConfig(
    val dictionary: String,
    val reject: Boolean = false,
    override val enabled: Boolean = true
) : EnabledConfig
