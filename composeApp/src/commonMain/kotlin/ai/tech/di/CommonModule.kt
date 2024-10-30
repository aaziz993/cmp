package ai.tech.di

import ai.tech.core.presentation.event.navigator.DefaultNavigator
import ai.tech.core.presentation.event.navigator.Navigator
import ai.tech.navigation.presentation.Destination
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
public class CommonModule {
    @Single
    public fun provideJson(): Json = Json { isLenient = true; ignoreUnknownKeys = true }

//    @Single
//    fun provideHttpClient(json: Json) = HttpClient {
//        install(ContentNegotiation) {
//            json(json)
//        }
//        if (enableNetworkLogs) {
//            install(Logging) {
//                logger = Logger.DEFAULT
//                level = LogLevel.NONE
//            }
//        }
//    }

    @Single
    public fun provideNavigator(): Navigator<Destination> = DefaultNavigator(Destination.HomeGraph.Main)
}