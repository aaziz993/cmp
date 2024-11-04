package ai.tech.core.misc.model.config

import ai.tech.core.data.validator.model.UserValidatorConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ValidatorConfig(
    val user: UserValidatorConfig
)
