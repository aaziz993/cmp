package ai.tech.core.misc.plugin.auth

import io.ktor.server.application.ApplicationCall

public interface ValidateAuthProvider<T : Any> {

    public suspend fun validate(call: ApplicationCall, credential: T): Any?
}
