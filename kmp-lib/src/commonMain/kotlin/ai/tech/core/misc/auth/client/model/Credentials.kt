package ai.tech.core.misc.auth.client.model

import kotlinx.serialization.Serializable

@Serializable
public data class Credentials(
    val username: String,
    val password: String,
)
