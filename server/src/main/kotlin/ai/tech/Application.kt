package ai.tech

import ai.tech.core.misc.model.config.server.ServerConfig
import ai.tech.core.misc.plugin.applicationmonitoring.configureApplicationMonitoring
import ai.tech.core.misc.plugin.auth.configureAuth
import ai.tech.core.misc.plugin.authheadresponse.configureAutoHeadResponse
import ai.tech.core.misc.plugin.cachingheaders.configureCachingHeaders
import ai.tech.core.misc.plugin.callid.configureCallId
import ai.tech.core.misc.plugin.calllogging.configureCallLogging
import ai.tech.core.misc.plugin.compression.configureCompression
import ai.tech.core.misc.plugin.conditionalheaders.configureConditionalHeaders
import ai.tech.core.misc.plugin.cors.configureCORS
import ai.tech.core.misc.plugin.dataconversion.configureDataConversion
import ai.tech.core.misc.plugin.defaultheaders.configureDefaultHeaders
import ai.tech.core.misc.plugin.dropwizardmetrics.configureDropwizardMetrics
import ai.tech.core.misc.plugin.forwardedheaders.configureForwardedHeaders
import ai.tech.core.misc.plugin.forwardedheaders.configureXForwardedHeaders
import ai.tech.core.misc.plugin.graphql.configureGraphQL
import ai.tech.core.misc.plugin.hsts.configureHSTS
import ai.tech.core.misc.plugin.httpsredirect.configureHttpsRedirect
import ai.tech.core.misc.plugin.micrometermetrics.configureMicrometerMetrics
import ai.tech.core.misc.plugin.partialcontent.configurePartialContent
import ai.tech.core.misc.plugin.ratelimit.configureRateLimit
import ai.tech.core.misc.plugin.resources.configureResources
import ai.tech.core.misc.plugin.routing.configureRouting
import ai.tech.core.misc.plugin.serialization.configureSerialization
import ai.tech.core.misc.plugin.session.configureSession
import ai.tech.core.misc.plugin.shutdown.configureShutdown
import ai.tech.core.misc.plugin.statuspages.configureStatusPages
import ai.tech.core.misc.plugin.swagger.configureSwagger
import ai.tech.core.misc.plugin.templating.configureFreeMarker
import ai.tech.core.misc.plugin.validation.configureRequestValidation
import ai.tech.core.misc.plugin.websockets.configureWebSockets
import ai.tech.core.misc.plugin.xhttpmethodoverride.configureXHttpMethodOverride
import ai.tech.plugin.routing
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import java.io.File
import org.koin.ksp.generated.*
import org.koin.ktor.ext.get

public fun main(args: Array<String>) {
    createKeystore()
    EngineMain.main(args)
}

@Suppress("unused")
public fun Application.module() {
//    configureKoin(AppConfig.read<ServerConfig> { readResourceText(it) }) {
//        defaultModule()
//    }

    val config = get<ServerConfig>()

    // Configure the Serialization plugin
    configureSerialization(config.serialization)

    // Configure the HttpsRedirect plugin
    configureHttpsRedirect(config.httpsRedirect, config.ktor.deployment.esslPort)

    // Configure the Routing plugin
    configureRouting(config.routing) {
        // Add other feature routes here
        routing(config)
    }

    // Configure the Websockets plugin
    configureWebSockets(
        config.websockets,
        if (config.ktor.deployment.esslPort == null) "ws://${config.ktor.deployment.host}:${config.ktor.deployment.eport}" else "wss://${config.ktor.deployment.host}:${config.ktor.deployment.esslPort}",
    )

    // Configure the Graphql plugin
    configureGraphQL(config.graphql)

    // Configure the CallLogging plugin
    configureCallLogging(config.callLogging)

    // Configure the CallLogging plugin
    configureCallId(config.callId)

    // Configure the RateLimit plugin
    configureRateLimit(config.rateLimit)

    // Configure the CORS plugin
    configureCORS(config.cors)

    // Configure the compression plugin
    configureCompression(config.compression)

    // Configure the PartialContent plugin
    configurePartialContent(config.partialContent)

    // Configure the HttpsRedirect plugin
    configureDataConversion(config.dataConversion)

    // Configure the validation plugin
    configureRequestValidation(config.validation)

    // Configure the Resources plugin
    configureResources(config.resources)

    // Configure the status pages plugin
    configureStatusPages(config.statusPages)

    // Configure the DefaultHeaders plugin
    configureDefaultHeaders(config.defaultHeaders)

    // Configure the CachingHeaders plugin
    configureCachingHeaders(config.cachingHeaders)

    // Configure the ConditionalHeaders plugin
    configureConditionalHeaders(config.conditionalHeaders)

    // Configure the ForwardedHeaders plugin
    configureForwardedHeaders(config.forwardedHeaders)

    // Configure the XForwardedHeaders plugin
    configureXForwardedHeaders(config.xForwardedHeaders)

    // Configure the HSTS plugin
    configureHSTS(config.hsts)

    // Configure the AutoHeadResponse plugin
    configureAutoHeadResponse(config.autoHeadResponse)

    // Configure the XHttpMethodOverride plugin
    configureXHttpMethodOverride(config.xHttpMethodOverride)

    // Configure session with cookies
    configureSession(config.auth)

    // Configure the security plugin with JWT
    configureAuth(config.ktor.deployment.address, get(), config.auth)

    // Configure the FreeMarker plugin for templating .ftl files
    configureFreeMarker(config.freeMarker)

    // Configure the Swagger plugin
    configureSwagger(config.swagger)

    // Configure the Application monitoring plugin
    configureApplicationMonitoring(config.applicationMonitoring)

    // Configure the MicrometerMetrics plugin
    configureMicrometerMetrics(config.micrometerMetrics)

    // Configure the DropwizardMetrics plugin
    configureDropwizardMetrics(config.dropwizardMetrics)

    // Configure the Shutdown plugin
    configureShutdown(config.shutdown)
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
