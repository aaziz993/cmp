package ai.tech.di

import ai.tech.core.presentation.event.navigator.DefaultNavigator
import ai.tech.core.presentation.event.navigator.Navigator
import ai.tech.navigation.presentation.Destination
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
public class CommonModule(private val enableNetworkLogs: Boolean) {
    @Single
    public fun provideJson(): Json = Json { isLenient = true; ignoreUnknownKeys = true }

    @Single
    public fun provideHttpClient(json: Json): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
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
}