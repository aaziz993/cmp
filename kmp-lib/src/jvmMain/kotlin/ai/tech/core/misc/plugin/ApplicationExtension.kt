package ai.tech.core.misc.plugin

import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.model.config.server.ServerConfig
import ai.tech.core.misc.network.http.client.createHttpClient
import ai.tech.core.misc.plugin.applicationmonitoring.configureApplicationMonitoring
import ai.tech.core.misc.plugin.auth.configureAuth
import ai.tech.core.misc.plugin.authheadresponse.configureAutoHeadResponse
import ai.tech.core.misc.plugin.cachingheaders.configureCachingHeaders
import ai.tech.core.misc.plugin.callid.configureCallId
import ai.tech.core.misc.plugin.calllogging.configureCallLogging
import ai.tech.core.misc.plugin.compression.configureCompression
import ai.tech.core.misc.plugin.conditionalheaders.configureConditionalHeaders
import ai.tech.core.misc.plugin.consul.configureConsul
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
import ai.tech.core.misc.plugin.freemarker.configureFreeMarker
import ai.tech.core.misc.plugin.koin.configureKoin
import ai.tech.core.misc.plugin.validation.configureRequestValidation
import ai.tech.core.misc.plugin.websockets.configureWebSockets
import ai.tech.core.misc.plugin.xhttpmethodoverride.configureXHttpMethodOverride
import com.apurebase.kgraphql.GraphQL
import freemarker.template.Configuration
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.metrics.dropwizard.DropwizardMetricsConfig
import io.ktor.server.metrics.micrometer.MicrometerMetricsConfig
import io.ktor.server.plugins.cachingheaders.CachingHeadersConfig
import io.ktor.server.plugins.callid.CallIdConfig
import io.ktor.server.plugins.calllogging.CallLoggingConfig
import io.ktor.server.plugins.compression.CompressionConfig
import io.ktor.server.plugins.conditionalheaders.ConditionalHeadersConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.server.plugins.cors.CORSConfig
import io.ktor.server.plugins.defaultheaders.DefaultHeadersConfig
import io.ktor.server.plugins.forwardedheaders.ForwardedHeadersConfig
import io.ktor.server.plugins.forwardedheaders.XForwardedHeadersConfig
import io.ktor.server.plugins.hsts.HSTSConfig
import io.ktor.server.plugins.httpsredirect.HttpsRedirectConfig
import io.ktor.server.plugins.methodoverride.XHttpMethodOverrideConfig
import io.ktor.server.plugins.partialcontent.PartialContentConfig
import io.ktor.server.plugins.ratelimit.RateLimitConfig
import io.ktor.server.plugins.requestvalidation.RequestValidationConfig
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.routing.Routing
import io.ktor.server.sessions.SessionsConfig
import io.ktor.server.websocket.WebSockets
import kotlin.collections.orEmpty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.ktor.ext.get

public fun Application.configure(
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
    val httpClient = createHttpClient() {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            )
        }
    }

    runBlocking {
        val fileConfigs = FileConfigService {
            readResourceText(it)
        }.readConfigs()

        val consulConfigs = ConsulConfigService(httpClient) {
            readResourceText(it)
        }.readConfigs()

        val configs = fileConfigs.mapValues { (profile, configs) -> configs + consulConfigs[profile].orEmpty() }

    }

    val config: ServerConfig = get()

    configureKoin(config, koinApplication)

    with(config) {
        // Configure consul
        configureConsul(httpClient, consul)

        // Configure the Serialization plugin
        configureSerialization(serialization, serializationBlock)

        // Configure the HttpsRedirect plugin
        configureHttpsRedirect(httpsRedirect, ktor.deployment.sslPort, httpsRedirectBlock)

        // Configure the Routing plugin
        configureRouting(routing, routingBlock)

        // Configure the Websockets plugin
        configureWebSockets(websockets, ktor.deployment.wsURL, websocketsBlock)

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
            ktor.deployment.httpURL, get(), auth,
            { provider, database, principalTable, roleTable ->
                null
            },
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

        // Configure the Shutdown plugin
        configureShutdown(shutdown, shutdownBlock)
    }
}
