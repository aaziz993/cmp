package ai.tech.core.misc.consul.client

import ai.tech.core.misc.consul.client.acl.AclClient
import ai.tech.core.misc.consul.client.agent.AgentClient
import ai.tech.core.misc.consul.client.catalog.CatalogClient
import ai.tech.core.misc.consul.client.coordinate.CoordinateClient
import ai.tech.core.misc.consul.client.event.EventClient
import ai.tech.core.misc.consul.client.health.HealthClient
import ai.tech.core.misc.consul.client.kv.KVClient
import ai.tech.core.misc.consul.client.operator.OperatorClient
import ai.tech.core.misc.consul.client.session.SessionClient
import ai.tech.core.misc.consul.client.snapshot.SnapshotClient
import ai.tech.core.misc.consul.client.status.StatusClient
import ai.tech.core.misc.network.http.client.configApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

public class ConsulClient(
    httpClient: HttpClient,
    public val address: String,
    public val aclToken: String? = null
) {

    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.configApi {
            if (aclToken != null) {
                defaultRequest {
                    header(HttpHeaders.Authorization, "Bearer $aclToken")
                }
            }
        },
    ).baseUrl(address).build()

    public val acl: AclClient = AclClient(ktorfit)
    public val agent: AgentClient = AgentClient(ktorfit)
    public val catalog: CatalogClient = CatalogClient(ktorfit)
    public val coordinate: CoordinateClient = CoordinateClient(ktorfit)
    public val event: EventClient = EventClient(ktorfit)
    public val health: HealthClient = HealthClient(ktorfit)
    public val kv: KVClient = KVClient(ktorfit)
    public val `operator`: OperatorClient = OperatorClient(ktorfit)
    public val session: SessionClient = SessionClient(ktorfit)
    public val snapshot: SnapshotClient = SnapshotClient(ktorfit)
    public val status: StatusClient = StatusClient(ktorfit)
}



