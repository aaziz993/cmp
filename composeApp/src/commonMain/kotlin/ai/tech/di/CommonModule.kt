package ai.tech.di

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
}