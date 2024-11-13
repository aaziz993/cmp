package ai.tech.core.misc.consul.client.model.config

import ai.tech.core.misc.consul.client.model.Connect
import ai.tech.core.misc.consul.client.model.ServiceHealth
import ai.tech.core.misc.consul.client.model.ServiceProxy
import ai.tech.core.misc.consul.client.model.ServiceWeights

public class ConsulPluginServiceConfig {

    public lateinit var name: String
    public var id: String? = null
    public var tags: List<String>? = null
    public var address: String? = null
    public var taggedAddress: Map<String, *>? = null
    public var meta: Map<String, String>? = null
    public var port: Int? = null
    public var kind: String? = null
    public var proxy: ServiceProxy? = null
    public var connect: Connect? = null
    public var check: ServiceHealth? = null
    public var enableTagOverride: Boolean? = null
    public var weights: ServiceWeights? = null
    public var replaceExistingChecks: Boolean? = null
    public var loadBalancer: LoadBalancer = takeFirstHealthy()
}
