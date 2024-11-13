package ai.tech.core.misc.consul.client

import ai.tech.core.misc.consul.client.health.AgentClient
import ai.tech.core.misc.consul.client.model.config.ConsulConfig
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class Consul(
    httpClient: HttpClient,
    public val address: String,
) {

    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.config {
            defaultRequest {
                url(address)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }
        },
    ).baseUrl(address).build()

    public val agent: AgentClient = ai.tech.core.misc.consul.client.health.AgentClient(this.httpClient)
    public val catalog: CatalogClient = CatalogClient(this.httpClient)
    public val config: ConfigClient = ConfigClient(this.httpClient)
    public val session: SessionClient = SessionClient(this.httpClient)
    public val kv: KVClient = KVClient(this.httpClient)
}



