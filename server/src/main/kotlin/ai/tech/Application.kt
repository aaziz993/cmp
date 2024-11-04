package ai.tech

import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.model.config.server.ServerConfig
import ai.tech.core.misc.plugin.auth.configureAuth
import ai.tech.core.misc.plugin.calllogging.configureCallLogging
import ai.tech.core.misc.plugin.compression.configureCompression
import ai.tech.core.misc.plugin.di.configureKoin
import ai.tech.core.misc.plugin.routing.configureRouting
import ai.tech.core.misc.plugin.serialization.configureSerialization
import ai.tech.core.misc.plugin.session.configureSession
import ai.tech.core.misc.plugin.statuspages.configureStatusPages
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import java.io.File
import org.koin.ksp.generated.*
import org.koin.ktor.ext.get

public fun main(args: Array<String>) {
    createKeystore()
    EngineMain.main(args)
}

@Suppress("unused")
public fun Application.module() {
    configureKoin(AppConfig.read<ServerConfig> { readResourceText(it) }) {
        defaultModule()
    }

    val config = get<ServerConfig>()

    configureCallLogging(config.callLogging)

    configureSerialization(config.serialization)

    configureCompression(config.compression)

    configureSession(config.auth)

    configureStatusPages(config.statusPages)

    configureAuth(config.ktor.deployment.address, get(), config.auth)

    configureRouting(config.routing) {  }

    // Configure the Serialization plugin
    appConfig.config.serialization?.let { configureSerialization(it) }

    // Configure the HttpsRedirect plugin
    appConfig.config.httpsRedirect?.let { configureHttpsRedirect(it, appConfig.sslPort) }

    // Configure the Routing plugin
    appConfig.config.routing?.let { configureRouting(it) }

    // Configure the Websockets plugin
    appConfig.config.websockets?.let {
        configureWebSockets(
            it,
            if (appConfig.sslPort == null) "ws://${appConfig.baseConfig.host}:${appConfig.baseConfig.port}" else "wss://${appConfig.baseConfig.host}:${appConfig.sslPort}"
        )
    }

    // Configure the Graphql plugin
    appConfig.config.graphql?.let { configureGraphQL(it) }

    // Configure the CallLogging plugin
    appConfig.config.callLogging?.let { configureCallLogging(it) }

    // Configure the CallLogging plugin
    appConfig.config.callId?.let { configureCallId(it) }

    // Configure the RateLimit plugin
    appConfig.config.rateLimit?.let { configureRateLimit(it) }

    // Configure the CORS plugin
    appConfig.config.cors?.let { configureCors(it) }

    // Configure the compression plugin
    appConfig.config.compression?.let { configureCompression(it) }

    // Configure the PartialContent plugin
    appConfig.config.partialContent?.let { configurePartialContent(it) }

    // Configure the HttpsRedirect plugin
    appConfig.config.dataConversion?.let { configureDataConversion(it) }

    // Configure the validation plugin
    appConfig.config.validation?.let { configureValidation(it) }

    // Configure the Resources plugin
    appConfig.config.resources?.let { configureResources(it) }

    // Configure the Locations plugin
    appConfig.config.locations?.let { configureLocations(it) }

    // Configure the status pages plugin
    appConfig.config.statusPages?.let { configureStatusPages(it) }

    // Configure the DefaultHeaders plugin
    appConfig.config.defaultHeaders?.let { configureDefaultHeaders(it) }

    // Configure the CachingHeaders plugin
    appConfig.config.cachingHeaders?.let { configureCachingHeaders(it) }

    // Configure the ConditionalHeaders plugin
    appConfig.config.conditionalHeaders?.let { configureConditionalHeaders(it) }

    // Configure the ForwardedHeaders plugin
    appConfig.config.forwardedHeaders?.let { configureForwardedHeaders(it) }

    // Configure the HSTS plugin
    appConfig.config.hsts?.let { configureHSTS(it) }

    // Configure the AutoHeadResponse plugin
    appConfig.config.autoHeadResponse?.let { configureAutoHeadResponse(it) }

    // Configure the XHttpMethodOverride plugin
    appConfig.config.xHttpMethodOverride?.let { configureXHttpMethodOverride(it) }

    // Configure session with cookies
    appConfig.config.session?.let { configureSession(it, appConfig.config.security) }

    // Configure the security plugin with JWT
    appConfig.config.security?.let {
        configureSecurity(
            it,
            if (appConfig.sslPort == null) "http://${appConfig.baseConfig.host}:${appConfig.baseConfig.port}" else "https://${appConfig.baseConfig.host}:${appConfig.sslPort}"
        )
    }

    // Configure the FreeMarker plugin for templating .ftl files
    appConfig.config.freeMarker?.let { configureFreeMarker(it) }

    // Configure the Swagger plugin
    appConfig.config.swagger?.let { configureSwagger(it) }

    // Configure the Application monitoring plugin
    appConfig.config.applicationMonitoring?.let { configureApplicationMonitoring(it) }

    // Configure the MicrometerMetrics plugin
    appConfig.config.micrometerMetrics?.let { configureMicrometerMetrics(it) }

    // Configure the DropwizardMetrics plugin
    appConfig.config.dropwizardMetrics?.let { configureDropwizardMetrics(it) }

    // Configure the Shutdown plugin
    appConfig.config.shutdown?.let { configureShutdown(it) }
}

private fun createKeystore() {
    val keyStoreFile = File("server/src/main/resources/cert/keystore.p12")

    if (keyStoreFile.exists()) return

    val keyStore = buildKeyStore {
        certificate("applicationTLS") {
            password = "AITech"
            daysValid = 365
            keySizeInBits = 4096
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }

    keyStore.saveToFile(keyStoreFile, "AITech")
}
