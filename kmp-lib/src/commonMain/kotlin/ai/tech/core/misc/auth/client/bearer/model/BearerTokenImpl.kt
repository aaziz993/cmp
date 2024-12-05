package ai.tech.core.misc.auth.client.bearer.model

import kotlinx.serialization.Serializable

@Serializable
public data class BearerTokenImpl(
    override val token: String,
    override val refreshToken: String? = null,
) : BearerToken
