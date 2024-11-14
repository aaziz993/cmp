package ai.tech.core.misc.consul.client.operator

import ai.tech.core.misc.consul.client.operator.model.RaftConfiguration
import de.jensklingenberg.ktorfit.Ktorfit

public class OperatorClient internal constructor(ktorfit: Ktorfit) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: OperatorApi = ktorfit.createOperatorApi()

    public suspend fun getRaftConfiguration(): RaftConfiguration =
        api.getConfiguration()

    public suspend fun getRaftConfiguration(datacenter: String): RaftConfiguration =
        api.getConfiguration(mapOf("dc" to datacenter))

    public suspend fun getStaleRaftConfiguration(datacenter: String): RaftConfiguration =
        api.getConfiguration(
            mapOf(
                "dc" to datacenter,
                "stale" to "true",
            ),
        )

    public suspend fun getStaleRaftConfiguration(): RaftConfiguration =
        api.getConfiguration(
            mapOf(
                "stale" to "true",
            ),
        )

    public suspend fun deletePeer(address: String): Unit = api.deletePeer(address)

    public suspend fun deletePeer(address: String, datacenter: String): Unit =
        api.deletePeer(address, mapOf("dc" to datacenter))
}
