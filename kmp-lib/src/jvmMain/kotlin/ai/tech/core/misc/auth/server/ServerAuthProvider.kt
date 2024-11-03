package ai.tech.core.misc.auth.server

import io.ktor.server.application.*
import io.ktor.server.auth.*

public interface ServerAuthProvider<T : Any> {

    public val name: String

    public fun roles(principal: T): Set<String> = emptySet()

    public fun skip(call: ApplicationCall): Boolean = false
}
