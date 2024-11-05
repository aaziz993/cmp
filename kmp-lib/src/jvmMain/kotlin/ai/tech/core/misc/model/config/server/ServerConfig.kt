package ai.tech.core.misc.model.config.server

import ai.tech.core.misc.auth.server.model.config.ServerAuthConfig
import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.model.config.di.KoinConfig
import ai.tech.core.misc.plugin.applicationmonitoring.model.config.ApplicationMonitoringConfig
import ai.tech.core.misc.plugin.authheadresponse.model.config.AutoHeadResponseConfig
import ai.tech.core.misc.plugin.cachingheaders.model.config.CachingHeadersConfig
import ai.tech.core.misc.plugin.callid.model.config.CallIdConfig
import ai.tech.core.misc.plugin.calllogging.model.config.CallLoggingConfig
import ai.tech.core.misc.plugin.compression.model.config.CompressionConfig
import ai.tech.core.misc.plugin.conditionalheaders.model.config.ConditionalHeadersConfig
import ai.tech.core.misc.plugin.cors.model.config.CORSConfig
import ai.tech.core.misc.plugin.dataconversion.model.config.DataConversionConfig
import ai.tech.core.misc.plugin.forwardedheaders.mode.config.ForwardedHeadersConfig
import ai.tech.core.misc.plugin.forwardedheaders.mode.config.XForwardedHeadersConfig
import ai.tech.core.misc.plugin.graphql.model.config.GraphQLConfig
import ai.tech.core.misc.plugin.hsts.model.config.HSTSConfig
import ai.tech.core.misc.plugin.httpsredirect.model.config.HTTPSRedirectConfig
import ai.tech.core.misc.plugin.partialcontent.model.config.PartialContentConfig
import ai.tech.core.misc.plugin.ratelimit.model.config.RateLimitsConfig
import ai.tech.core.misc.plugin.resources.model.config.ResourcesConfig
import ai.tech.core.misc.plugin.routing.model.config.RoutingConfig
import ai.tech.core.misc.plugin.serialization.model.config.SerializationConfig
import ai.tech.core.misc.plugin.session.model.config.SessionEncryptConfig
import ai.tech.core.misc.plugin.shutdown.model.config.ShutDownConfig
import ai.tech.core.misc.plugin.statuspages.model.config.StatusPagesConfig
import ai.tech.core.misc.plugin.swagger.model.config.SwaggerConfig
import ai.tech.core.misc.plugin.templating.model.config.FreeMarkerConfig
import ai.tech.core.misc.plugin.validation.model.config.RequestValidationConfig
import ai.tech.core.misc.plugin.websockets.model.config.WebSocketsConfig
import ai.tech.core.misc.plugin.xhttpmethodoverride.model.config.XHttpMethodOverrideConfig
import ai.tech.core.misc.plugin.dropwizardmetrics.model.config.DropwizardMetricsConfig
import ai.tech.core.misc.plugin.micrometermetrics.model.config.MicrometerMetricsConfig
import ai.tech.core.misc.plugin.defaultheaders.model.config.DefaultHeadersConfig

public interface ServerConfig : Config {

    public val koin: KoinConfig?
    public val serialization: SerializationConfig?
    public val httpsRedirect: HTTPSRedirectConfig?
    public val routing: RoutingConfig?
    public val websockets: WebSocketsConfig?
    public val graphql: GraphQLConfig?
    public val callLogging: CallLoggingConfig?
    public val callId: CallIdConfig?
    public val rateLimit: RateLimitsConfig?
    public val cors: CORSConfig?
    public val compression: CompressionConfig?
    public val partialContent: PartialContentConfig?
    public val dataConversion: DataConversionConfig?
    public val validation: RequestValidationConfig?
    public val resources: ResourcesConfig?
    public val statusPages: StatusPagesConfig?
    public val defaultHeaders: DefaultHeadersConfig?
    public val cachingHeaders: CachingHeadersConfig?
    public val conditionalHeaders: ConditionalHeadersConfig?
    public val forwardedHeaders: ForwardedHeadersConfig?
    public val xForwardedHeaders: XForwardedHeadersConfig?
    public val hsts: HSTSConfig?
    public val autoHeadResponse: AutoHeadResponseConfig?
    public val xHttpMethodOverride: XHttpMethodOverrideConfig?
    public val session: SessionEncryptConfig?
    public val auth: ServerAuthConfig?
    public val freeMarker: FreeMarkerConfig?
    public val swagger: SwaggerConfig?
    public val applicationMonitoring: ApplicationMonitoringConfig?
    public val micrometerMetrics: MicrometerMetricsConfig?
    public val dropwizardMetrics: DropwizardMetricsConfig?
    public val shutdown: ShutDownConfig?
}
