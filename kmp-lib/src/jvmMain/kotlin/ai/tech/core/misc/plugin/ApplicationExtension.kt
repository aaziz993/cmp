package ai.tech.core.misc.plugin

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.server.ServerConfig
import ai.tech.core.misc.model.config.server.ServerHostConfig
import ai.tech.core.misc.plugin.applicationmonitoring.configureApplicationMonitoring
import ai.tech.core.misc.plugin.auth.configureAuth
import ai.tech.core.misc.plugin.authheadresponse.configureAutoHeadResponse
import ai.tech.core.misc.plugin.cachingheaders.configureCachingHeaders
import ai.tech.core.misc.plugin.callid.configureCallId
import ai.tech.core.misc.plugin.calllogging.configureCallLogging
import ai.tech.core.misc.plugin.cohort.configureCohort
import ai.tech.core.misc.plugin.compression.configureCompression
import ai.tech.core.misc.plugin.conditionalheaders.configureConditionalHeaders
import ai.tech.core.misc.plugin.consul.configureConsulDiscovery
import ai.tech.core.misc.plugin.cors.configureCORS
import ai.tech.core.misc.plugin.dataconversion.configureDataConversion
import ai.tech.core.misc.plugin.defaultheaders.configureDefaultHeaders
import ai.tech.core.misc.plugin.dropwizardmetrics.configureDropwizardMetrics
import ai.tech.core.misc.plugin.forwardedheaders.configureForwardedHeaders
import ai.tech.core.misc.plugin.forwardedheaders.configureXForwardedHeaders
import ai.tech.core.misc.plugin.freemarker.configureFreeMarker
import ai.tech.core.misc.plugin.graphql.configureGraphQL
import ai.tech.core.misc.plugin.hsts.configureHSTS
import ai.tech.core.misc.plugin.httpsredirect.configureHttpsRedirect
import ai.tech.core.misc.plugin.koin.configureKoin
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
import ai.tech.core.misc.plugin.validation.configureRequestValidation
import ai.tech.core.misc.plugin.websockets.configureWebSockets
import ai.tech.core.misc.plugin.xhttpmethodoverride.configureXHttpMethodOverride
import com.apurebase.kgraphql.GraphQL
import freemarker.template.Configuration
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.TaskManager.Companion.host
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.dropwizard.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.plugins.methodoverride.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import java.io.File
import org.koin.core.KoinApplication
import org.koin.ktor.ext.get
import org.lighthousegames.logging.logging

private val appLog = logging("Application")

public fun ApplicationEngine.Configuration.configure(config: ServerHostConfig) {

    config.connectionGroupSize?.let { connectionGroupSize = it }
    config.workerGroupSize?.let { workerGroupSize = it }
    config.callGroupSize?.let { callGroupSize = it }
    config.shutdownGracePeriod?.let { shutdownGracePeriod = it }
    config.shutdownTimeout?.let { shutdownTimeout = it }

    connector {
        port = config.port
    }

    config.ssl?.let {
        sslConnector(
            keyStore = it.keyStore,
            keyAlias = it.keyAlias,
            keyStorePassword = { it.keyStorePassword.toCharArray() },
            privateKeyPassword = { it.privateKeyPassword.toCharArray() },
        ) {
            port = it.port
            keyStorePath = File(it.keyStorePath)
        }
    }
}

public fun Application.configure(
    config: ServerConfig,
    koinApplication: KoinApplication.() -> Unit = {},
    serializationBlock: (ContentNegotiationConfig.() -> Unit)? = null,
    httpsRedirectBlock: (HttpsRedirectConfig.() -> Unit)? = null,
    routingBlock: (Routing.() -> Unit)? = null,
    websocketsBlock: (WebSockets.WebSocketOptions.() -> Unit)? = null,
    graphQLBlock: (GraphQL.Configuration.() -> Unit)? = null,
    callLoggingBlock: (CallLoggingConfig.() -> Unit)? = null,
    callIdBlock: (CallIdConfig.() -> Unit)? = null,
    rateLimitBlock: (RateLimitConfig.() -> Unit)? = null,
    corsBlock: (CORSConfig.() -> Unit)? = null,
    compressionBlock: (CompressionConfig.() -> Unit)? = null,
    partialContentBlock: (PartialContentConfig.() -> Unit)? = null,
    requestValidationBlock: (RequestValidationConfig.() -> Unit)? = null,
    statusPagesBlock: (StatusPagesConfig.() -> Unit)? = null,
    defaultHeadersBlock: (DefaultHeadersConfig.() -> Unit)? = null,
    cachingHeadersBlock: (CachingHeadersConfig.() -> Unit)? = null,
    conditionalHeadersBlock: (ConditionalHeadersConfig.() -> Unit)? = null,
    forwardedHeadersBlock: (ForwardedHeadersConfig.() -> Unit)? = null,
    xForwardedHeadersBlock: (XForwardedHeadersConfig.() -> Unit)? = null,
    hstsBlock: (HSTSConfig.() -> Unit)? = null,
    xHttpMethodOverrideBlock: (XHttpMethodOverrideConfig.() -> Unit)? = null,
    sessionBlock: (SessionsConfig.() -> Unit)? = null,
    authBlock: (AuthenticationConfig.() -> Unit)? = null,
    freeMarkerBlock: (Configuration.() -> Unit)? = null,
    swaggerBlock: (PluginConfigDsl.() -> Unit)? = null,
    micrometerMetricsBlock: (MicrometerMetricsConfig.() -> Unit)? = null,
    dropwizardMetricsBlock: (DropwizardMetricsConfig.() -> Unit)? = null,
    shutdownBlock: (ShutDownUrl.Config.() -> Unit)? = null
) {
    configureKoin(config, koinApplication)

    var healthChecks: Map<String, String>? = null

    with(config) {
        with(host) {
            // Configure the Serialization plugin
            configureSerialization(serialization, serializationBlock)

            // Configure the HttpsRedirect plugin
            configureHttpsRedirect(httpsRedirect, ssl?.port, httpsRedirectBlock)

            // Configure the Routing plugin
            configureRouting(routing) {
                consul?.takeIf(EnabledConfig::enable)?.discovery?.takeIf(EnabledConfig::enable)?.let {
                    get(it.healthCheckPath) { call.respond(HttpStatusCode.OK) }
                }
                routingBlock?.invoke(this)
            }

            // Configure the Websockets plugin
            configureWebSockets(websockets, preferredWSSURL, websocketsBlock)

            // Configure the Graphql plugin
            configureGraphQL(graphql, graphQLBlock)

            // Configure the CallLogging plugin
            configureCallLogging(callLogging, callLoggingBlock)

            // Configure the CallLogging plugin
            configureCallId(callId, callIdBlock)

            // Configure the RateLimit plugin
            configureRateLimit(rateLimit, rateLimitBlock)

            // Configure the CORS plugin
            configureCORS(cors, corsBlock)

            // Configure the compression plugin
            configureCompression(compression, compressionBlock)

            // Configure the PartialContent plugin
            configurePartialContent(partialContent, partialContentBlock)

            // Configure the HttpsRedirect plugin
            configureDataConversion(dataConversion)

            // Configure the validation plugin
            configureRequestValidation(validation, requestValidationBlock)

            // Configure the Resources plugin
            configureResources(resources)

            // Configure the status pages plugin
            configureStatusPages(statusPages, statusPagesBlock)

            // Configure the DefaultHeaders plugin
            configureDefaultHeaders(defaultHeaders, defaultHeadersBlock)

            // Configure the CachingHeaders plugin
            configureCachingHeaders(cachingHeaders, cachingHeadersBlock)

            // Configure the ConditionalHeaders plugin
            configureConditionalHeaders(conditionalHeaders, conditionalHeadersBlock)

            // Configure the ForwardedHeaders plugin
            configureForwardedHeaders(forwardedHeaders, forwardedHeadersBlock)

            // Configure the XForwardedHeaders plugin
            configureXForwardedHeaders(xForwardedHeaders, xForwardedHeadersBlock)

            // Configure the HSTS plugin
            configureHSTS(hsts, hstsBlock)

            // Configure the AutoHeadResponse plugin
            configureAutoHeadResponse(autoHeadResponse)

            // Configure the XHttpMethodOverride plugin
            configureXHttpMethodOverride(xHttpMethodOverride, xHttpMethodOverrideBlock)

            // Configure session with cookies
            configureSession(auth) {
//            it.jwtHs256.filterValues(EnabledConfig::enable).forEach { (name, config) ->
//                cookie<UserSession>(name, config.cookie?.takeIf(EnabledConfig::enable))
//            }
//
//            it.jwtRs256.filterValues(EnabledConfig::enable).forEach { (name, config) ->
//                cookie<UserSession>(name, config.cookie?.takeIf(EnabledConfig::enable))
//            }
//
//            it.oauth.filterValues(EnabledConfig::enable).forEach { (name, config) ->
//                cookie<UserSession>(name, config.cookie?.takeIf(EnabledConfig::enable))
//            }

                sessionBlock?.invoke(this)
            }

            // Configure the security plugin with JWT
            configureAuth(
                preferredHttpsURL,
                get(),
                auth,
                { provider, database, principalTable, roleTable -> null },
                authBlock,
            )

            // Configure the FreeMarker plugin for templating .ftl files
            configureFreeMarker(freeMarker, freeMarkerBlock)

            // Configure the Swagger plugin
            configureSwagger(swagger, swaggerBlock)

            // Configure the Application monitoring plugin
            configureApplicationMonitoring(applicationMonitoring)

            // Configure the MicrometerMetrics plugin
            configureMicrometerMetrics(micrometerMetrics, micrometerMetricsBlock)

            // Configure the DropwizardMetrics plugin
            configureDropwizardMetrics(dropwizardMetrics, dropwizardMetricsBlock)

            // Configure the Cohort health checks plugin
            healthChecks = configureCohort(cohort, auth?.takeIf(EnabledConfig::enable)?.oauth, database)

            // Configure the Shutdown plugin
            configureShutdown(shutdown, shutdownBlock)
        }

        consul?.takeIf(EnabledConfig::enable)?.let {
            configureConsulDiscovery(
                get(),
                it.address,
                it.discovery,
                host.preferredHttpsURL,
                host.preferredSslPort,
                application,
                healthChecks,
            ) { exception, attempt ->
                appLog.w(exception) { "Couldn't register in consul \"${it.address}\" in attempt \"$attempt\"" }
            }
        }
    }
}
