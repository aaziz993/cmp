package ai.tech.di.module

import ai.tech.core.data.keyvalue.SettingsKeyValue
import ai.tech.core.misc.auth.client.ClientAuthService
import ai.tech.core.misc.auth.keycloak.KeycloakService
import ai.tech.core.misc.location.localization.AbstractLocalizationService
import ai.tech.core.misc.location.localization.MapLocalizationService
import ai.tech.core.misc.location.localization.weblate.WeblateService
import ai.tech.core.misc.location.localization.weblate.client.WeblateClient
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.client.ClientConfig
import ai.tech.core.misc.network.http.client.createHttpClient
import ai.tech.core.presentation.event.navigator.DefaultNavigator
import ai.tech.core.presentation.event.navigator.Navigator
import ai.tech.navigation.presentation.Destination
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("ai.tech")
public class CommonModule {

    @Single
    public fun provideJson(): Json = Json { isLenient = true; ignoreUnknownKeys = true }

    @Single
    public fun provideHttpClient(config: ClientConfig, json: Json): HttpClient = with(config.ktorClient) {
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
    public fun provideNavigator(): Navigator<Destination> = DefaultNavigator(Destination.Main)

    @Single
    public fun provideLocalizationProvider(
        config: ClientConfig,
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

    @Single
    public fun provideAuthProvider(
        config: ClientConfig,
        httpClient: HttpClient,
        keyValue: SettingsKeyValue
    ): ClientAuthService {
        require(config.auth.providerConfig.provider == "keycloak") {
            "Only keycloak auth provider is supported for now."
        }

        return KeycloakService(
            httpClient,
            config.auth.provider,
            config.auth.providerConfig,
            keyValue,
        )
    }
}
