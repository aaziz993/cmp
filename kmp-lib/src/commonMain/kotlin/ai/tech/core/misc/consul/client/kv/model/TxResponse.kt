package ai.tech.core.misc.consul.client.kv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TxResponse(
    @SerialName("Results")
val results: List<Map<String, Value>>,
    @SerialName("Errors")
val errors: List<TxError>
)
