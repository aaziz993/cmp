package ai.tech.core.misc.plugin.statuspages

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import ai.tech.core.misc.auth.model.exception.UnauthorizedAccessException
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.statuspages.model.config.StatusPagesConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

public fun Application.configureStatusPages(config: StatusPagesConfig?, block: (io.ktor.server.plugins.statuspages.StatusPagesConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.plugins.statuspages.StatusPagesConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            exception<RequestValidationException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
            }

            exception<NumberFormatException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, "${cause.message}. The input param is not a valid number")
            }

            exception<IllegalArgumentException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, "${cause.message}")
            }

            exception<UnauthenticatedAccessException> { call, cause ->
                call.respond(HttpStatusCode.Unauthorized, cause.message.toString())
            }

            exception<UnauthorizedAccessException> { call, cause ->
                call.respond(HttpStatusCode.Forbidden, cause.message.toString())
            }

            exception<Throwable> { call, cause ->
                call.respond(HttpStatusCode.InternalServerError, cause.message.toString())
            }

            it.status?.filter(EnabledConfig::enabled)?.forEach {
                status(*it.codes.toTypedArray()) { call, status ->
                    call.respondText(text = it.text, status = status)
                }
            }

            it.statusFile?.filter(EnabledConfig::enabled)?.forEach {
                statusFile(*it.codes.toTypedArray(), filePattern = it.filePattern)
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(StatusPages) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
