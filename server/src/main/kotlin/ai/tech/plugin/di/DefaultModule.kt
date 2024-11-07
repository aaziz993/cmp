package ai.tech.plugin.di

import ai.tech.core.misc.consul.Consul
import ai.tech.core.misc.location.localization.AbstractLocalizationService
import ai.tech.core.misc.location.localization.weblate.WeblateClient
import ai.tech.core.misc.location.localization.weblate.WeblateService
import ai.tech.core.misc.model.config.server.ServerConfig
import ai.tech.core.misc.network.http.client.createHttpClient
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.collections.get
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
public class DefaultModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Single
    public fun provideHttpClient(): HttpClient =
        createHttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
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

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
        }

    @Single
    public fun provideConsul(
        config: ServerConfig,
        httpClient: HttpClient,
    ): Consul = Consul(httpClient, config.consul)

    @Single
    public fun provideLocalizationProvider(
        config: ServerConfig,
        httpClient: HttpClient,
    ): AbstractLocalizationService =
        WeblateService(
            WeblateClient(
                httpClient,
                config.localization.weblate[config.localizationProvider]!!,
            ),
            config.project,
        )
}
