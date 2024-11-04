package ai.tech.core.misc.plugin.hsts

import ai.tech.core.misc.plugin.hsts.model.config.HSTSConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.hsts.*

public fun Application.configureHSTS(config: HSTSConfig?, block: (io.ktor.server.plugins.hsts.HSTSConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.hsts.HSTSConfig.() -> Unit)? = config?.takeIf { it.enable != false }?.let {
        {
            it.global?.let {
                it.preload?.let { }
                it.includeSubDomains?.let { includeSubDomains = it }
                it.maxAgeInSeconds?.let { maxAgeInSeconds = it }
                it.customDirectives?.let { customDirectives + it }
            }

            it.hostSpecific?.forEach {
                withHost(it.key) {
                    it.value.preload?.let { }
                    it.value.includeSubDomains?.let { includeSubDomains = it }
                    it.value.maxAgeInSeconds?.let { maxAgeInSeconds = it }
                    it.value.customDirectives?.let { customDirectives + it }
                }
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(HSTS) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
