package ai.tech.core.misc.consul

import ai.tech.core.misc.consul.module.config.ConsulConfig
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
    private val consulConfig: ConsulConfig
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient: HttpClient = httpClient.config {
        defaultRequest {
            url(consulConfig.address)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            )
        }
    }

    public val agent: AgentClient = AgentClient(this.httpClient)
    public val catalog: CatalogClient = CatalogClient(this.httpClient)
    public val config: ConfigClient = ConfigClient(this.httpClient)
    public val session: SessionClient = SessionClient(this.httpClient)
    public val kv: KVClient = KVClient(this.httpClient)
}



