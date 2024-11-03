package ai.tech.core.misc.plugin

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import ai.tech.core.misc.auth.model.exception.UnauthorizedAccessException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

public fun Application.configStatusPages(config: StatusPagesConfig?) {
    config?.takeIf { it.enable != false }?.let {
        install(StatusPages) {
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

            it.status?.filter { it.enable != false }?.forEach {
                status(*it.codes.toTypedArray()) { call, status ->
                    call.respondText(text = it.text, status = status)
                }
            }

            it.statusFile?.filter { it.enable != false }?.forEach {
                statusFile(*it.codes.toTypedArray(), filePattern = it.filePattern)
            }
        }
    }
}
