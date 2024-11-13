package ai.tech.core.misc.consul.client.model.config

import ai.tech.core.misc.consul.client.model.Connect
import ai.tech.core.misc.consul.client.model.ServiceHealth
import ai.tech.core.misc.consul.client.model.ServiceProxy
import ai.tech.core.misc.consul.client.model.ServiceWeights
import kotlinx.serialization.Serializable

@Serializable
public data class ConsulServiceConfig(
    val name: String,
    val id: String? = null,
    val tags: List<String>? = null,
    val address: String? = null,
    val taggedAddress: Map<String, *>? = null,
    val meta: Map<String, String>? = null,
    val port: Int? = null,
    val kind: String? = null,
    val proxy: ServiceProxy? = null,
    val connect: Connect? = null,
    val check: ServiceHealth? = null,
    val enableTagOverride: Boolean? = null,
    val weights: ServiceWeights? = null,
    val replaceExistingChecks: Boolean? = null
)
