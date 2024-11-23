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
    override val enable: Boolean = true
) : EnabledConfig
