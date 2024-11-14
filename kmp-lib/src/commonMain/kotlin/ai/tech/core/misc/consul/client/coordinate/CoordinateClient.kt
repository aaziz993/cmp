package ai.tech.core.misc.consul.client.coordinate

import com.google.common.collect.ImmutableMap
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
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: CoordinateApi = ktorfit.createCoordinateApi()

    public fun getDatacenters(): List<Datacenter> {
        return api.getDatacenters()
    }

    public fun getNodes(dc: String): List<Coordinate> {
        return api.getNodes(dcQuery(dc))
    }

    public fun getNodes(): List<Coordinate> {
        return getNodes(null)
    }

    private fun dcQuery(dc: String): Map<String, String> {
        return if (dc != null) ImmutableMap.of("dc", dc) else Collections.emptyMap()
    }
}
