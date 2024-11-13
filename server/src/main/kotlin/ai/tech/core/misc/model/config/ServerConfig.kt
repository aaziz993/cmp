package ai.tech.core.misc.model.config

import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProvidersConfig
import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.client.KtorClientConfig
import ai.tech.core.misc.model.config.di.KoinConfig
import ai.tech.core.misc.model.config.server.KtorServerConfig
import ai.tech.core.misc.model.config.server.ServerConfig
import ai.tech.core.misc.plugin.applicationmonitoring.model.config.ApplicationMonitoringConfig
import ai.tech.core.misc.plugin.authheadresponse.model.config.AutoHeadResponseConfig
import ai.tech.core.misc.plugin.cachingheaders.model.config.CachingHeadersConfig
import ai.tech.core.misc.plugin.callid.model.config.CallIdConfig
import ai.tech.core.misc.plugin.calllogging.model.config.CallLoggingConfig
import ai.tech.core.misc.plugin.compression.model.config.CompressionConfig
import ai.tech.core.misc.plugin.conditionalheaders.model.config.ConditionalHeadersConfig
import ai.tech.core.misc.plugin.cors.model.config.CORSConfig
import ai.tech.core.misc.plugin.dataconversion.model.config.DataConversionConfig
import ai.tech.core.misc.plugin.defaultheaders.model.config.DefaultHeadersConfig
import ai.tech.core.misc.plugin.dropwizardmetrics.model.config.DropwizardMetricsConfig
import ai.tech.core.misc.plugin.forwardedheaders.mode.config.ForwardedHeadersConfig
import ai.tech.core.misc.plugin.forwardedheaders.mode.config.XForwardedHeadersConfig
import ai.tech.core.misc.plugin.graphql.model.config.GraphQLConfig
import ai.tech.core.misc.plugin.hsts.model.config.HSTSConfig
import ai.tech.core.misc.plugin.httpsredirect.model.config.HTTPSRedirectConfig
import ai.tech.core.misc.plugin.micrometermetrics.model.config.MicrometerMetricsConfig
import ai.tech.core.misc.plugin.partialcontent.model.config.PartialContentConfig
import ai.tech.core.misc.plugin.ratelimit.model.config.RateLimitsConfig
import ai.tech.core.misc.plugin.resources.model.config.ResourcesConfig
import ai.tech.core.misc.plugin.routing.model.config.RoutingConfig
import ai.tech.core.misc.plugin.serialization.model.config.SerializationConfig
import ai.tech.core.misc.plugin.shutdown.model.config.ShutDownConfig
import ai.tech.core.misc.plugin.statuspages.model.config.StatusPagesConfig
import ai.tech.core.misc.plugin.swagger.model.config.SwaggerConfig
import ai.tech.core.misc.plugin.freemarker.model.config.FreeMarkerConfig
import ai.tech.core.misc.plugin.validation.model.config.RequestValidationConfig
import ai.tech.core.misc.plugin.websockets.model.config.WebSocketsConfig
import ai.tech.core.misc.plugin.xhttpmethodoverride.model.config.XHttpMethodOverrideConfig
import ai.tech.core.presentation.model.config.ServerPresentationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ServerConfig(
    override val koin: KoinConfig? = null,
    override val ktorClient: KtorClientConfig = KtorClientConfig(),
    override val consul: ConsulConfig? = null,
    override val database: Map<String, DatabaseProviderConfig>? = null,
    override val serialization: SerializationConfig? = null,
    override val httpsRedirect: HTTPSRedirectConfig? = null,
    override val routing: RoutingConfig? = null,
    override val websockets: WebSocketsConfig? = null,
    override val graphql: GraphQLConfig? = null,
    override val callLogging: CallLoggingConfig? = null,
    override val callId: CallIdConfig? = null,
    override val rateLimit: RateLimitsConfig? = null,
    override val cors: CORSConfig? = null,
    override val compression: CompressionConfig? = null,
    override val partialContent: PartialContentConfig? = null,
    override val dataConversion: DataConversionConfig? = null,
    override val validation: RequestValidationConfig? = null,
    override val resources: ResourcesConfig? = null,
    override val statusPages: StatusPagesConfig? = null,
    override val defaultHeaders: DefaultHeadersConfig? = null,
    override val cachingHeaders: CachingHeadersConfig? = null,
    override val conditionalHeaders: ConditionalHeadersConfig? = null,
    override val forwardedHeaders: ForwardedHeadersConfig? = null,
    override val xForwardedHeaders: XForwardedHeadersConfig? = null,
    override val hsts: HSTSConfig? = null,
    override val autoHeadResponse: AutoHeadResponseConfig? = null,
    override val xHttpMethodOverride: XHttpMethodOverrideConfig? = null,
    override val auth: AuthProvidersConfig? = null,
    override val freeMarker: FreeMarkerConfig? = null,
    override val swagger: SwaggerConfig? = null,
    override val applicationMonitoring: ApplicationMonitoringConfig? = null,
    override val micrometerMetrics: MicrometerMetricsConfig? = null,
    override val dropwizardMetrics: DropwizardMetricsConfig? = null,
    override val shutdown: ShutDownConfig? = null,
    override val project: String,
    override val localization: LocalizationConfig,
    override val validator: ValidatorConfig,
    override val ktor: KtorServerConfig,
    val presentation: ServerPresentationConfig?
) : ServerConfig
