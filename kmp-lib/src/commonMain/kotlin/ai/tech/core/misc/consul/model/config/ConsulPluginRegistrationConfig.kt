package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.consul.client.agent.model.Registration

public class ConsulRegistrationConfig {

    public lateinit var registration: Registration
    public var replaceExistingChecks: Boolean? = null
    public var loadBalancer: LoadBalancer = takeFirstHealthy()
}
