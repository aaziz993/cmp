package ai.tech.core.misc.plugin.statuspages.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.type.serialization.serializer.http.HttpStatusCodeSerial
import kotlinx.serialization.Serializable

@Serializable
public data class StatusConfig(
    val codes: List<HttpStatusCodeSerial>,
    val text: String,
    override val enabled: Boolean = true,
) : EnabledConfig
