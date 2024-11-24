package ai.tech.core.misc.plugin.validation.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class RequestValidationConfig(
    override val enabled: Boolean = true,
) : EnabledConfig
