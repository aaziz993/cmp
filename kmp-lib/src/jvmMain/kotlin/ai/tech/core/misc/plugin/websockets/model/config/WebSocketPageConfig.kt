package ai.tech.core.misc.plugin.websockets.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class WebSocketPageConfig(
    val uri: String? = null,
    val filePath: String? = null,
)
