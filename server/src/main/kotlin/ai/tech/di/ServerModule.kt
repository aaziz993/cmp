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
import io.ktor.client.plugins.cache.HttpCache
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
                this@with.timeout?.takeIf(EnabledConfig::enabled)?.let {
                    install(HttpTimeout) {
                        it.requestTimeoutMillis?.let { requestTimeoutMillis }
                        it.connectTimeoutMillis.let { connectTimeoutMillis }
                        it.socketTimeoutMillis.let { socketTimeoutMillis }
                    }
                }

                this@with.cache?.takeIf(EnabledConfig::enabled)?.let {
                    install(HttpCache) {
                        it.isShared?.let { isShared = it }
                    }
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

                log?.takeIf(EnabledConfig::enabled)?.let {
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
        weblate?.takeIf(EnabledConfig::enabled)?.let {
            WeblateService(
                WeblateClient(httpClient, it.address, it.apiKey),
                config.application.name,
                foundLanguage,
            )
        } ?: MapLocalizationService(foundLanguage, localization)
    }
}
