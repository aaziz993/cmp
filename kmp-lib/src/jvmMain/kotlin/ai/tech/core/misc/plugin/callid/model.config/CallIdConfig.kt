package ai.tech.core.misc.plugin.callid.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CallIdConfig(
    val verify: CallIdVerifyConfig? = null,
    val header: String? = null,
    val retrieveFromHeader: String? = null,
    val replyToHeader: String? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
