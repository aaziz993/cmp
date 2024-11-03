package ai.tech.core.misc.config.server

import ai.tech.core.data.environment.getEnv
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class KtorServerDeploymentConfig(
    val host: String,
    val port: String?,
    val ssl: String? = null,
) {
    @Transient
    val eport: Int = port?.split(":")?.let {
        if (it.size == 1) {
            it[0]
        } else {
            getEnv(it[0]) ?: it[1]
        }.toInt()
    } ?: 8080

    @Transient
    val esslPort: Int? = ssl?.split(":")?.let {
        if (it.size == 1) {
            it[0]
        } else {
            getEnv(it[0]) ?: it[1]
        }.toInt()
    }

    @Transient
    val address: String = esslPort?.let { "https://$host:$it" } ?: "http://$host:$port"
}
