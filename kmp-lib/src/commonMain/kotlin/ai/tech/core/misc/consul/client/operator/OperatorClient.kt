package ai.tech.core.misc.consul.client.operator

import com.google.common.collect.ImmutableMap
import de.jensklingenberg.ktorfit.Ktorfit

public class OperatorClient internal constructor(ktorfit: Ktorfit){
    /**
     * Constructs an instance of this class.
     *
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: OperatorApi = ktorfit.createOperatorApi()

    public fun getRaftConfiguration(): RaftConfiguration {
        return api.getConfiguration(ImmutableMap.of())
    }

    public fun getRaftConfiguration(datacenter: String): RaftConfiguration {
        return api.getConfiguration(ImmutableMap.of("dc", datacenter))
    }

    public fun getStaleRaftConfiguration(datacenter: String): RaftConfiguration {
        return http.extract(
            api.getConfiguration(
                ImmutableMap.of(
                    "dc", datacenter, "stale", "true"
                )
            )
        )
    }

    public fun getStaleRaftConfiguration(): RaftConfiguration {
        return http.extract(
            api.getConfiguration(
                ImmutableMap.of(
                    "stale", "true"
                )
            )
        )
    }

    public fun deletePeer(address: String) {
        api.deletePeer(address, ImmutableMap.of())
    }

    public fun deletePeer(address: String, datacenter: String) {
        api.deletePeer(address, ImmutableMap.of("dc", datacenter))
    }
}
