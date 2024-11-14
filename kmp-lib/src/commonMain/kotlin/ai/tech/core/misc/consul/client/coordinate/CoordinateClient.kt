package ai.tech.core.misc.consul.client.coordinate

import ai.tech.core.misc.consul.client.coordinate.model.Coordinate
import ai.tech.core.misc.consul.client.coordinate.model.Datacenter
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * HTTP Client for /v1/coordinate/ endpoints.
 *
 * @see [The Consul API Docs](http://www.consul.io/docs/agent/http.html.agent)
 */
public class CoordinateClient internal constructor(ktorfit: Ktorfit) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: CoordinateApi = ktorfit.createCoordinateApi()

    public suspend fun getDatacenters(): List<Datacenter> = api.getDatacenters()

    public suspend fun getNodes(dc: String?=null): List<Coordinate> =api.getNodes(dc?.let { mapOf("dc" to dc) }.orEmpty())
}