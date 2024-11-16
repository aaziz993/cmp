package ai.tech.core.misc.type.model

import ai.tech.core.data.validator.Validator
import ai.tech.core.misc.type.validator
import kotlinx.serialization.descriptors.SerialDescriptor

public interface Property {

    public val name: String
    public val descriptor: SerialDescriptor
    public val readOnly: Boolean
    public val validator: Validator?
}
