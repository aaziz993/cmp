package ai.tech.core.misc.plugin.validation

import ai.tech.core.misc.plugin.validation.model.config.ValidationConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

/**
 * Configure the validation plugin
 * https://ktor.io/docs/request-validation.html
 * We extend the validation with our own rules in separate file in validators package
 * like routes
 */
public fun Application.configureValidation(config: ValidationConfig) {
    if (config.enable == true) {
        install(RequestValidation) {
//            userValidation() // User validation
        }
    }
}
