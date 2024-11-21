package ai.tech.core.misc.model.config.server

import ai.tech.core.data.database.model.config.DBProviderConfig
import ai.tech.core.misc.model.config.Config
import ai.tech.core.misc.model.config.di.KoinConfig
import ai.tech.core.misc.plugin.applicationmonitoring.model.config.ApplicationMonitoringConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProvidersConfig
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
import ai.tech.core.misc.plugin.freemarker.model.config.FreeMarkerConfig
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
import ai.tech.core.misc.plugin.validation.model.config.RequestValidationConfig
import ai.tech.core.misc.plugin.websockets.model.config.WebSocketsConfig
import ai.tech.core.misc.plugin.xhttpmethodoverride.model.config.XHttpMethodOverrideConfig
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.network.tls.extensions.HashAlgorithm
import io.ktor.network.tls.extensions.SignatureAlgorithm
import java.io.File
import java.io.FileInputStream
import java.net.Inet4Address
import java.security.KeyStore
import javax.security.auth.x500.X500Principal

public interface ServerConfig : Config {

    public val koin: KoinConfig?
    public val database: Map<String, DBProviderConfig>?
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
    public val auth: AuthProvidersConfig?
    public val freeMarker: FreeMarkerConfig?
    public val swagger: SwaggerConfig?
    public val applicationMonitoring: ApplicationMonitoringConfig?
    public val micrometerMetrics: MicrometerMetricsConfig?
    public val dropwizardMetrics: DropwizardMetricsConfig?
    public val shutdown: ShutDownConfig?
}

public val SSLConfig.keyStore: KeyStore
    get() {
        val keyStoreFile = File(keyStorePath)

        if (!generate || (keyStoreFile.exists() && !rewrite)) {
            return KeyStore.getInstance(format).apply {
                load(FileInputStream(keyStoreFile), keyStorePassword.toCharArray())
            }
        }

        return buildKeyStore {
            certificate(keyAlias) {
                this@keyStore.hash?.let { hash = HashAlgorithm.valueOf(it) }
                this@keyStore.sign?.let { sign = SignatureAlgorithm.valueOf(it) }
                password = privateKeyPassword
                this@keyStore.subject?.let { subject = X500Principal(it) }
                this@keyStore.daysValid?.let { daysValid = it }
                this@keyStore.keySizeInBits?.let { keySizeInBits = it }
                this@keyStore.domains?.let { domains = it }
                this@keyStore.ipAddresses?.let { ipAddresses = it.map(Inet4Address::getByName) }
            }
        }.also { it.saveToFile(keyStoreFile, keyStorePassword) }
    }

