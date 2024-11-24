package ai.tech.core.misc.plugin.cohort.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CohortConfig(
    val heapDump: Boolean? = null,
    val operatingSystem: Boolean? = null,
    val memory: Boolean? = null,
    val jvmInfo: Boolean? = null,
    val gc: Boolean? = null,
    val threadDump: Boolean? = null,
    val sysprops: Boolean? = null,
    val endpointPrefix: String = "health",
    val oauthEndpointPrefix: String? = null,
    val dbEndpointPrefix: String? = null,
    override val enable: Boolean = true
) : EnabledConfig {

    private val oauthEndpointPrefixPart = oauthEndpointPrefix?.let { "$it/" }.orEmpty()

    private val dbEndpointPrefixPart = dbEndpointPrefix?.let { "$it/" }.orEmpty()

    public fun getOAuthEndpoint(path: String?): String = "$oauthEndpointPrefixPart${path.orEmpty()}"

    public fun getDBEndpoint(path: String?): String = "$dbEndpointPrefixPart${path.orEmpty()}"
}
