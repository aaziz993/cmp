package ai.tech.core.misc.plugin.validation

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.validation.model.config.RequestValidationConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

/**
 * Configure the validation plugin
 * https://ktor.io/docs/request-validation.html
 * We extend the validation with our own rules in separate file in validators package
 * like routes
 */
public fun Application.configureRequestValidation(config: RequestValidationConfig?, block: (io.ktor.server.plugins.requestvalidation.RequestValidationConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.requestvalidation.RequestValidationConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            //            userValidation() // User validation
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(RequestValidation) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
