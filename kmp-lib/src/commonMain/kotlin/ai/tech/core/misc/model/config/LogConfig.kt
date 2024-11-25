package ai.tech.core.misc.model.config

import kotlinx.serialization.Serializable
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel

@Serializable
public data class LogConfig(
    public val level: String? = null,
    override val enabled: Boolean = true,
) : EnabledConfig {

    public fun configureKmLogging() {
        KmLogging.setLogLevel(
            if (enabled) {
                level?.let { LogLevel.valueOf(it.lowercase().replaceFirstChar { it.uppercase() }) } ?: LogLevel.Info
            }
            else {
                LogLevel.Off
            },
        )
    }
}
