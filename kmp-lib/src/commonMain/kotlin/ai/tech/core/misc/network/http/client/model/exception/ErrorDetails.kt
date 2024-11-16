package ai.tech.core.misc.network.http.client.model.exception

import kotlinx.serialization.Serializable

@Serializable
public data class ErrorDetails(
    val code: Int,
    val message: String,
    val status: String,
)
