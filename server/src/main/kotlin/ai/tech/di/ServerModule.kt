package ai.tech.di

import ai.tech.core.misc.location.localization.AbstractLocalizationService
import ai.tech.core.misc.location.localization.MapLocalizationService
import ai.tech.core.misc.location.localization.weblate.WeblateService
import ai.tech.core.misc.location.localization.weblate.client.WeblateClient
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.server.ServerConfigImpl
import ai.tech.core.misc.network.http.client.createHttpClient
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("ai.tech")
public class ServerModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Single
    public fun provideHttpClient(config: ServerConfigImpl): HttpClient =
        with(config.httpClient) {
            createHttpClient {
                install(HttpTimeout) {
                    this@with.requestTimeoutMillis?.let { requestTimeoutMillis }
                    this@with.connectTimeoutMillis.let { connectTimeoutMillis }
                    this@with.socketTimeoutMillis.let { socketTimeoutMillis }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                            explicitNulls = false
                        },
                    )
                }

                log?.takeIf(EnabledConfig::enable)?.let {
                    install(Logging) {
                        logger = Logger.DEFAULT
                        it.level?.let { level = LogLevel.valueOf(it.uppercase()) }
                    }
                }
            }
        }

    @Single
    public fun provideLocalizationProvider(
        config: ServerConfigImpl,
        httpClient: HttpClient,
    ): AbstractLocalizationService = with(config.localization) {
        weblate?.takeIf(EnabledConfig::enable)?.let {
            WeblateService(
                WeblateClient(httpClient, it.address, it.apiKey),
                config.application.name,
                foundLanguage,
            )
        } ?: MapLocalizationService(foundLanguage, localization)
    }
}
