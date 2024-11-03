package ai.tech.core.misc.plugin.callid.model.config

import kotlinx.serialization.Serializable

@Serializable
public data  class CallIdVerifyConfig(
    val dictionary: String,
    val reject: Boolean = false,
)
