package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateCleartextMessageOptions(
    var text: String
)
