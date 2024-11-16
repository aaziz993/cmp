package ai.tech.core.misc.type.model;

import ai.tech.core.data.validator.Validator
import ai.tech.core.misc.type.validator
import kotlinx.serialization.descriptors.SerialDescriptor

public data class Property(
    val name: String,
    val descriptor: SerialDescriptor,
    val readOnly: Boolean = false,
    val validator: Validator? = descriptor.validator(),
)
