package ai.tech.core.misc.consul.client.agent.model

import ai.tech.core.misc.consul.client.catalog.model.ServiceWeights
import ai.tech.core.misc.consul.client.health.model.ServiceHealth
import ai.tech.core.misc.consul.client.model.Connect
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Registration(
    @SerialName("Name")
    val name: String,
    @SerialName("Id")
    val id: String? = null,
    @SerialName("Tags")
    val tags: List<String>? = null,
    @SerialName("Address")
    val address: String? = null,
    @SerialName("TaggedAddress")
    val taggedAddress: Map<String, *>? = null,
    @SerialName("Meta")
    val meta: Map<String, String>? = null,
    @SerialName("Port")
    val port: Int? = null,
    @SerialName("Kind")
    val kind: String? = null,
    @SerialName("Proxy")
    val proxy: ServiceProxy? = null,
    @SerialName("Connect")
    val connect: Connect? = null,
    @SerialName("Check")
    val check: ServiceHealth? = null,
    @SerialName("EnableTagOverride")
    val enableTagOverride: Boolean? = null,
    @SerialName("Weights")
    val weights: ServiceWeights? = null,
)
