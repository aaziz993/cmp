package ai.tech.core.misc.network.http.client.model.exception;

import kotlinx.serialization.Serializable

@Serializable
public data class ErrorInfo(val error: ErrorDetails)
