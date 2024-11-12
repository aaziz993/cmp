package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import ai.tech.core.misc.plugin.auth.model.config.ChallengeAuthProviderConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

public interface AbstractChallengeAuthProvider {

    public val config: ChallengeAuthProviderConfig

    public suspend fun challenge(call: ApplicationCall, vararg args: Any) {
        val message = "Token is not valid or has expired"

        if (config.exception) {
            throw UnauthenticatedAccessException(message)
        }

        call.respond(HttpStatusCode.Unauthorized, message)
    }
}
