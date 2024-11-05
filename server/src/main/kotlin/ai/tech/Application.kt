package ai.tech

import ai.tech.core.data.filesystem.readResourceText
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
import ai.tech.core.misc.plugin.di.configureKoin
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
import ai.tech.plugin.di.DefaultModule
import ai.tech.plugin.routing
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.koin.ksp.generated.*
import org.koin.ktor.ext.get

public fun main(args: Array<String>) {
    // Configure ssl certificate generation
//        val properties= Properties.dec readResourceText("application")
//        rootConfig.environment.config.decode<GenerateServerSSLConfig>("")?.generate()
    EngineMain.main(args)
}

@Suppress("unused")
public fun Application.module() {
    configureKoin(ServerConfig.read<ServerConfig> { readResourceText(it) }) {
        DefaultModule().module
    }

    with(get<ServerConfig>()) {

        // Configure the Serialization plugin
        configureSerialization(serialization)

        // Configure the HttpsRedirect plugin
        configureHttpsRedirect(httpsRedirect, ktor.deployment.sslPort)

        // Configure the Routing plugin
        configureRouting(routing) {
            // Add other feature routes here
            routing(this@with)
        }

        // Configure the Websockets plugin
        configureWebSockets(
            websockets,
            ktor.deployment.wsURL,
        )

        // Configure the Graphql plugin
        configureGraphQL(graphql)

        // Configure the CallLogging plugin
        configureCallLogging(callLogging)

        // Configure the CallLogging plugin
        configureCallId(callId)

        // Configure the RateLimit plugin
        configureRateLimit(rateLimit)

        // Configure the CORS plugin
        configureCORS(cors)

        // Configure the compression plugin
        configureCompression(compression)

        // Configure the PartialContent plugin
        configurePartialContent(partialContent)

        // Configure the HttpsRedirect plugin
        configureDataConversion(dataConversion)

        // Configure the validation plugin
        configureRequestValidation(validation)

        // Configure the Resources plugin
        configureResources(resources)

        // Configure the status pages plugin
        configureStatusPages(statusPages)

        // Configure the DefaultHeaders plugin
        configureDefaultHeaders(defaultHeaders)

        // Configure the CachingHeaders plugin
        configureCachingHeaders(cachingHeaders)

        // Configure the ConditionalHeaders plugin
        configureConditionalHeaders(conditionalHeaders)

        // Configure the ForwardedHeaders plugin
        configureForwardedHeaders(forwardedHeaders)

        // Configure the XForwardedHeaders plugin
        configureXForwardedHeaders(xForwardedHeaders)

        // Configure the HSTS plugin
        configureHSTS(hsts)

        // Configure the AutoHeadResponse plugin
        configureAutoHeadResponse(autoHeadResponse)

        // Configure the XHttpMethodOverride plugin
        configureXHttpMethodOverride(xHttpMethodOverride)

        // Configure session with cookies
        configureSession(auth)

        // Configure the security plugin with JWT
        configureAuth(ktor.deployment.httpURL, get(), auth)

        // Configure the FreeMarker plugin for templating .ftl files
        configureFreeMarker(freeMarker)

        // Configure the Swagger plugin
        configureSwagger(swagger)

        // Configure the Application monitoring plugin
        configureApplicationMonitoring(applicationMonitoring)

        // Configure the MicrometerMetrics plugin
        configureMicrometerMetrics(micrometerMetrics)

        // Configure the DropwizardMetrics plugin
        configureDropwizardMetrics(dropwizardMetrics)

        // Configure the Shutdown plugin
        configureShutdown(shutdown)
    }
}


