package ai.tech.core.misc.consul.client.status

import ai.tech.core.misc.consul.model.parameter.QueryParameters
import de.jensklingenberg.ktorfit.Ktorfit

public class StatusClient internal constructor(ktorfit: Ktorfit) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: StatusApi = ktorfit.createStatusApi()

    /**
     * Retrieves the host/port of the Consul leader.
     *
     * GET /v1/status/leader
     *
     * @param queryParameters The Query Options to use.
     * @return The host/port of the leader.
     */
    public suspend fun getLeader(queryParameters: QueryParameters = QueryParameters()): String =
        api.getLeader(queryParameters.query).replace("\"", "").trim()

    /**
     * Retrieves a list of host/ports for raft peers.
     *
     * GET /v1/status/peers
     *
     * @param queryParameters The Query Options to use.
     * @return List of host/ports for raft peers.
     */
    public suspend fun getPeers(queryParameters: QueryParameters = QueryParameters()): List<String> =
        api.getPeers(queryParameters.query)
}
