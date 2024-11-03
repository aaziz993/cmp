package ai.tech.core.misc.plugin.websockets.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.serialization.model.config.JsonConfig
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class WebSocketsConfig(
    val pingPeriod: Duration? = null,
    val timeout: Duration? = null,
    val maxFrameSize: Long? = null,
    val masking: Boolean? = null,
    val contentConverter: JsonConfig? = null,
    val page: WebSocketPageConfig? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
