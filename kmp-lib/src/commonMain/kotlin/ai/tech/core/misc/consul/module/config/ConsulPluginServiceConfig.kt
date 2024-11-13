package ai.tech.core.misc.consul.module.config

import ai.tech.core.misc.consul.module.Connect
import ai.tech.core.misc.consul.module.ServiceHealth
import ai.tech.core.misc.consul.module.ServiceProxy
import ai.tech.core.misc.consul.module.ServiceWeights

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
}
