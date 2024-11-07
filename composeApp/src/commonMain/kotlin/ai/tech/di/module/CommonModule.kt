package ai.tech.di.module

import ai.tech.core.misc.network.http.client.createHttpClient
import ai.tech.core.misc.auth.client.ClientAuthService
import ai.tech.core.misc.auth.client.keycloak.KeycloakService
import ai.tech.core.misc.auth.client.keycloak.KeycloakClient
import ai.tech.core.misc.location.localization.AbstractLocalizationService
import ai.tech.core.misc.location.localization.weblate.WeblateClient
import ai.tech.core.misc.location.localization.weblate.WeblateService
import ai.tech.core.misc.model.config.client.ClientConfig
import ai.tech.core.presentation.event.navigator.DefaultNavigator
import ai.tech.core.presentation.event.navigator.Navigator
import ai.tech.core.presentation.navigation.Destination
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ai.tech.core.data.keyvalue.SettingsKeyValue
import ai.tech.core.misc.consul.Consul

@Module
public class CommonModule(private val enableNetworkLogs: Boolean) {

    @Single
    public fun provideJson(): Json = Json { isLenient = true; ignoreUnknownKeys = true }

    @Single
    public fun provideHttpClient(json: Json): HttpClient = createHttpClient {
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

        if (enableNetworkLogs) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.NONE
            }
        }
    }

    @Single
    public fun provideNavigator(): Navigator<Destination> = DefaultNavigator(Destination.HomeGraph.Main)

    @Single
    public fun provideConsul(
        config: ClientConfig,
        httpClient: HttpClient,
    ): Consul = Consul(httpClient, config.consul)

    @Single
    public fun provideLocalizationProvider(
        config: ClientConfig,
        httpClient: HttpClient,
    ): AbstractLocalizationService =
        WeblateService(
            WeblateClient(
                httpClient,
                config.localization.weblate[config.localization.provider]!!,
            ),
            config.project,
        )

    @Single
    public fun provideAuthProvider(
        config: ClientConfig,
        httpClient: HttpClient,
        keyValue: SettingsKeyValue
    ): ClientAuthService = KeycloakService(
        KeycloakClient(httpClient, config.auth.oauth.entries.first { (_, config) -> config.provider == "keycloak" }.value),
        keyValue,
        config.auth.provider,
    )
}
