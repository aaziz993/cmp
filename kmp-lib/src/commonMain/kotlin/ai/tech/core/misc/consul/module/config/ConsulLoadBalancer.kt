package ai.tech.core.misc.consul.client.model.config

import ai.tech.core.misc.consul.client.model.ServiceHealth
import kotlin.also
import kotlin.collections.all
import kotlin.collections.firstOrNull
import kotlin.collections.getOrNull

public typealias LoadBalancer = List<ServiceHealth>.() -> ServiceHealth?

public fun isHealthy(health: ServiceHealth): Boolean {
    return health.checks.all { check ->
        check.status == "passing"
    }
}

public fun takeFirstHealthy(): LoadBalancer = {
    firstOrNull(::isHealthy)
}

public fun roundRobin(): LoadBalancer {
    var index = 0
    return {
        getOrNull(index)?.also {
            index = (index + 1) % size
        }
    }
}
