package ai.tech.core.misc.auth.model.bearer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class BearerTokenImpl(
    override val token: String,
    override val refreshToken: String,
) : BearerToken
