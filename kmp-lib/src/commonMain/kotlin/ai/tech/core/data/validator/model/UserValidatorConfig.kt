package ai.tech.core.data.validator.model

import ai.tech.core.data.validator.Validator
import kotlinx.serialization.Serializable

@Serializable
public data class UserValidatorConfig(
    val username: Validator = Validator.nonEmpty(),
    val firstName: Validator = Validator.nonEmpty(),
    val lastName: Validator = Validator.nonEmpty(),
    val phone: Validator = Validator.numericPhone(false),
    val email: Validator = Validator.email(),
)
