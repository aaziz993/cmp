package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.consul.client.health.model.ServiceHealth
import kotlin.also
import kotlin.collections.all
import kotlin.collections.firstOrNull
import kotlin.collections.getOrNull

public enum class LoadBalancer(private val balancer: List<ServiceHealth>.() -> ServiceHealth?) {
    FIRST_HEALTHY(takeFirstHealthy()),
    ROUND_ROBIN(roundRobin());

    public operator fun invoke(services: List<ServiceHealth>): ServiceHealth? = balancer(services)
}

public fun isHealthy(health: ServiceHealth): Boolean {
    return health.checks.all { check ->
        check.status == "passing"
    }
}

public fun takeFirstHealthy(): List<ServiceHealth>.() -> ServiceHealth? = {
    firstOrNull(::isHealthy)
}

public fun roundRobin(): List<ServiceHealth>.() -> ServiceHealth? {
    var index = 0
    return {
        getOrNull(index)?.also {
            index = (index + 1) % size
        }
    }
}
