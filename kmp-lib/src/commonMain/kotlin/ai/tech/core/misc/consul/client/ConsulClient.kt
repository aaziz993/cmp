package ai.tech.core.misc.consul.client

import ai.tech.core.misc.consul.client.acl.AclApi
import ai.tech.core.misc.consul.client.acl.createAclApi
import ai.tech.core.misc.consul.client.agent.AgentApi
import ai.tech.core.misc.consul.client.agent.createAgentApi
import ai.tech.core.misc.consul.client.catalog.CatalogApi
import ai.tech.core.misc.consul.client.catalog.createCatalogApi
import ai.tech.core.misc.consul.client.coordinate.CoordinateApi
import ai.tech.core.misc.consul.client.coordinate.createCoordinateApi
import ai.tech.core.misc.consul.client.event.EventApi
import ai.tech.core.misc.consul.client.event.createEventApi
import ai.tech.core.misc.consul.client.health.HealthApi
import ai.tech.core.misc.consul.client.health.createHealthApi
import ai.tech.core.misc.consul.client.keyvalue.KVApi
import ai.tech.core.misc.consul.client.keyvalue.createKVApi
import ai.tech.core.misc.consul.client.operator.OperatorApi
import ai.tech.core.misc.consul.client.operator.createOperatorApi
import ai.tech.core.misc.consul.client.session.SessionApi
import ai.tech.core.misc.consul.client.session.createSessionApi
import ai.tech.core.misc.consul.client.snapshot.SnapshotApi
import ai.tech.core.misc.consul.client.snapshot.createSnapshotApi
import ai.tech.core.misc.consul.client.status.StatusApi
import ai.tech.core.misc.consul.client.status.createStatusApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

public class ConsulClient(
    httpClient: HttpClient,
    public val address: String,
) {

    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.config {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }
        },
    ).baseUrl(address).build()

    public val acl: AclApi = ktorfit.createAclApi()
    public val agent: AgentApi = ktorfit.createAgentApi()
    public val catalog: CatalogApi = ktorfit.createCatalogApi()
    public val coordinate: CoordinateApi = ktorfit.createCoordinateApi()
    public val event: EventApi = ktorfit.createEventApi()
    public val health: HealthApi = ktorfit.createHealthApi()
    public val kv: KVApi = ktorfit.createKVApi()
    public val `operator`: OperatorApi = ktorfit.createOperatorApi()
    public val session: SessionApi = ktorfit.createSessionApi()
    public val snapshot: SnapshotApi = ktorfit.createSnapshotApi()
    public val status: StatusApi = ktorfit.createStatusApi()
}



