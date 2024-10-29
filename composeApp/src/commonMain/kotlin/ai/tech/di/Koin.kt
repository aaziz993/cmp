package ai.tech.di

import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.ksp.generated.*

public fun koinConfiguration(): KoinApplication = koinApplication {
    printLogger()

    modules(
        CommonModule().module,
        PlatformModule().module
    )
}