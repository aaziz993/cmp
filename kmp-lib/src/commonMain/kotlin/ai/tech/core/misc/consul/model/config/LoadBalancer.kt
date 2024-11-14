package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.consul.client.health.model.ServiceHealth
import kotlin.also
import kotlin.collections.all
import kotlin.collections.firstOrNull
import kotlin.collections.getOrNull

public enum class LoadBalancer {
    FIRST_HEALTHY,
    ROUND_ROBIN,
}

public fun isHealthy(health: ServiceHealth): Boolean {
    return health.checks.all { check ->
        check.status == "passing"
    }
}

public fun takeFirstHealthy():  List<ServiceHealth>.() -> ServiceHealth? = {
    firstOrNull(::isHealthy)
}

public fun roundRobin():  List<ServiceHealth>.() -> ServiceHealth? {
    var index = 0
    return {
        getOrNull(index)?.also {
            index = (index + 1) % size
        }
    }
}

public fun loadBalancer(type: LoadBalancer):  List<ServiceHealth>.() -> ServiceHealth? = when (type) {
    LoadBalancer.FIRST_HEALTHY -> takeFirstHealthy()
    LoadBalancer.ROUND_ROBIN -> roundRobin()
}
