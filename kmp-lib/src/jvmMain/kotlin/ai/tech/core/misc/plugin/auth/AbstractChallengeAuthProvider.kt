package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import ai.tech.core.misc.plugin.auth.model.config.ChallengeAuthProviderConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

public abstract class AbstractChallengeAuthProvider(private val config: ChallengeAuthProviderConfig) {

    public open suspend fun challenge(call: ApplicationCall) {
        val message = "Token is not valid or has expired"

        if (config.exception) {
            throw UnauthenticatedAccessException(message)
        }

        call.respond(HttpStatusCode.Unauthorized, message)
    }
}
