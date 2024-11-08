package ai.tech.core.misc.plugin.websockets.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class WebSocketPageConfig(
    val uri: String,
    val wsURI: String,
    override val enable: Boolean = true,
) : EnabledConfig
