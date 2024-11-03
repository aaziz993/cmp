package ai.tech.core.misc.plugin.partialcontent.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class PartialContentConfig(
    val maxRangeCount: Int? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
