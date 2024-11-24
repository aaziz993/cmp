package ai.tech.core.misc.model.config.server

import ai.tech.core.data.database.model.config.DBConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.model.config.di.KoinConfig
import ai.tech.core.misc.plugin.applicationmonitoring.model.config.ApplicationMonitoringConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProvidersConfig
import ai.tech.core.misc.plugin.authheadresponse.model.config.AutoHeadResponseConfig
import ai.tech.core.misc.plugin.cachingheaders.model.config.CachingHeadersConfig
import ai.tech.core.misc.plugin.callid.model.config.CallIdConfig
import ai.tech.core.misc.plugin.calllogging.model.config.CallLoggingConfig
import ai.tech.core.misc.plugin.cohort.model.config.CohortConfig
import ai.tech.core.misc.plugin.compression.model.config.CompressionConfig
import ai.tech.core.misc.plugin.conditionalheaders.model.config.ConditionalHeadersConfig
import ai.tech.core.misc.plugin.cors.model.config.CORSConfig
import ai.tech.core.misc.plugin.dataconversion.model.config.DataConversionConfig
import ai.tech.core.misc.plugin.defaultheaders.model.config.DefaultHeadersConfig
import ai.tech.core.misc.plugin.dropwizardmetrics.model.config.DropwizardMetricsConfig
import ai.tech.core.misc.plugin.forwardedheaders.mode.config.ForwardedHeadersConfig
import ai.tech.core.misc.plugin.forwardedheaders.mode.config.XForwardedHeadersConfig
import ai.tech.core.misc.plugin.freemarker.model.config.FreeMarkerConfig
import ai.tech.core.misc.plugin.graphql.model.config.GraphQLConfig
import ai.tech.core.misc.plugin.hsts.model.config.HSTSConfig
import ai.tech.core.misc.plugin.httpsredirect.model.config.HTTPSRedirectConfig
import ai.tech.core.misc.plugin.ktorservertaskscheduling.model.config.KtorServerTaskSchedulingConfig
import ai.tech.core.misc.plugin.micrometermetrics.model.config.MicrometerMetricsConfig
import ai.tech.core.misc.plugin.partialcontent.model.config.PartialContentConfig
import ai.tech.core.misc.plugin.ratelimit.model.config.RateLimitsConfig
import ai.tech.core.misc.plugin.resources.model.config.ResourcesConfig
import ai.tech.core.misc.plugin.routing.model.config.RoutingConfig
import ai.tech.core.misc.plugin.serialization.model.config.SerializationConfig
import ai.tech.core.misc.plugin.shutdown.model.config.ShutDownConfig
import ai.tech.core.misc.plugin.statuspages.model.config.StatusPagesConfig
import ai.tech.core.misc.plugin.swagger.model.config.SwaggerConfig
import ai.tech.core.misc.plugin.validation.model.config.RequestValidationConfig
import ai.tech.core.misc.plugin.websockets.model.config.WebSocketsConfig
import ai.tech.core.misc.plugin.xhttpmethodoverride.model.config.XHttpMethodOverrideConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class ServerHostConfig(
    public val application: ApplicationConfig,
    val koin: KoinConfig? = null,
    val taskScheduling: KtorServerTaskSchedulingConfig?=null,
    val database: Map<String?, DBConfig> = emptyMap(),
    val serialization: SerializationConfig? = null,
    val httpsRedirect: HTTPSRedirectConfig? = null,
    val routing: RoutingConfig? = null,
    val websockets: WebSocketsConfig? = null,
    val graphql: GraphQLConfig? = null,
    val callLogging: CallLoggingConfig? = null,
    val callId: CallIdConfig? = null,
    val rateLimit: RateLimitsConfig? = null,
    val cors: CORSConfig? = null,
    val compression: CompressionConfig? = null,
    val partialContent: PartialContentConfig? = null,
    val dataConversion: DataConversionConfig? = null,
    val validation: RequestValidationConfig? = null,
    val resources: ResourcesConfig? = null,
    val statusPages: StatusPagesConfig? = null,
    val defaultHeaders: DefaultHeadersConfig? = null,
    val cachingHeaders: CachingHeadersConfig? = null,
    val conditionalHeaders: ConditionalHeadersConfig? = null,
    val forwardedHeaders: ForwardedHeadersConfig? = null,
    val xForwardedHeaders: XForwardedHeadersConfig? = null,
    val hsts: HSTSConfig? = null,
    val autoHeadResponse: AutoHeadResponseConfig? = null,
    val xHttpMethodOverride: XHttpMethodOverrideConfig? = null,
    val auth: AuthProvidersConfig? = null,
    val freeMarker: FreeMarkerConfig? = null,
    val swagger: SwaggerConfig? = null,
    val applicationMonitoring: ApplicationMonitoringConfig? = null,
    val micrometerMetrics: MicrometerMetricsConfig? = null,
    val dropwizardMetrics: DropwizardMetricsConfig? = null,
    val cohort: CohortConfig? = null,
    val shutdown: ShutDownConfig? = null,
    val watchPaths: List<String> = listOf("classes", "resources"),
    val connectionGroupSize: Int? = null,
    val workerGroupSize: Int? = null,
    val callGroupSize: Int? = null,
    val shutdownGracePeriod: Long? = null,
    val shutdownTimeout: Long? = null,
    override val host: String,
    override val port: Int = 8080,
    val ssl: SSLConfig? = null,
) : SharedHostConfig {

    @Transient
    public val preferredSslPort: Int = ssl?.port ?: port

    @Transient
    public val preferredHttpsURL: String = ssl?.port?.let { "https://$host:$it" } ?: "http://$host:$port"

    @Transient
    public val preferredWSSURL: String = ssl?.port?.let { "wss://$host:$it" } ?: "ws://$host:$port"
}
