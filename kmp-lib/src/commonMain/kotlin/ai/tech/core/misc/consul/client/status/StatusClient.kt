package ai.tech.core.misc.consul.client.status

import de.jensklingenberg.ktorfit.Ktorfit

public class StatusClient internal constructor(ktorfit: Ktorfit){
    /**
     * Constructs an instance of this class.
     *
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: StatusApi = ktorfit.createStatusApi()

    /**
     * Retrieves the host/port of the Consul leader.
     *
     * GET /v1/status/leader
     *
     * @return The host/port of the leader.
     */
    public fun getLeader(): String {
        return getLeader(QueryOptions.BLANK)
    }

    /**
     * Retrieves the host/port of the Consul leader.
     *
     * GET /v1/status/leader
     *
     * @param queryOptions The Query Options to use.
     * @return The host/port of the leader.
     */
    public fun getLeader(queryOptions: QueryOptions): String {
        return http.extract(api.getLeader(queryOptions.toQuery())).replace("\"", "").trim()
    }

    /**
     * Retrieves a list of host/ports for raft peers.
     *
     * GET /v1/status/peers
     *
     * @return List of host/ports for raft peers.
     */
    public fun getPeers(): List<String> {
        return getPeers(QueryOptions.BLANK)
    }

    /**
     * Retrieves a list of host/ports for raft peers.
     *
     * GET /v1/status/peers
     *
     * @param queryOptions The Query Options to use.
     * @return List of host/ports for raft peers.
     */
    public fun getPeers(queryOptions: QueryOptions): List<String> {
        return http.extract(api.getPeers(queryOptions.toQuery()))
    }
}
