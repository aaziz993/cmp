package ai.tech.core.misc.plugin.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

public interface ChallengeAuthProvider {
    public suspend fun challenge(call: ApplicationCall, vararg args: Any): Unit =
        call.respond(HttpStatusCode.Unauthorized, "Credentials are not valid")
}
