package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import io.ktor.server.application.*

public interface ChallengeAuthProvider {

    public suspend fun challenge(call: ApplicationCall, vararg args: Any) {
        throw UnauthenticatedAccessException()
    }
}
