package ai.tech.di.module

import ai.tech.core.data.keyvalue.SettingsKeyValue
import ai.tech.core.misc.auth.client.ClientAuthService
import ai.tech.core.misc.auth.keycloak.KeycloakService
import ai.tech.core.misc.location.localization.AbstractLocalizationService
import ai.tech.core.misc.location.localization.MapLocalizationService
import ai.tech.core.misc.location.localization.weblate.WeblateService
import ai.tech.core.misc.location.localization.weblate.client.WeblateClient
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.client.ClientConfigImpl
import ai.tech.core.misc.network.http.client.createHttpClient
import ai.tech.core.presentation.event.navigator.DefaultNavigator
import ai.tech.core.presentation.event.navigator.Navigator
import ai.tech.navigation.presentation.Destination
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
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
    public fun provideHttpClient(config: ClientConfigImpl, json: Json): HttpClient = with(config.httpClient) {
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
    public fun provideNavigator(): Navigator<Destination> = DefaultNavigator(Destination.Main)

    @Single
    public fun provideLocalizationProvider(
        config: ClientConfigImpl,
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

    @Single
    public fun provideAuthProvider(
        config: ClientConfigImpl,
        httpClient: HttpClient,
        keyValue: SettingsKeyValue
    ): ClientAuthService = with(config.ui.auth) {
        require(providerConfig.provider == "keycloak") {
            "Only keycloak auth provider is supported for now."
        }

        return KeycloakService(
            providerName,
            httpClient,
            providerConfig.address,
            providerConfig.realm,
            providerConfig.clientId,
            keyValue,
        )
    }
}
