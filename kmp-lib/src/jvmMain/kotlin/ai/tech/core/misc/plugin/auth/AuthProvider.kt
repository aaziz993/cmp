package ai.tech.core.misc.plugin.auth

import io.ktor.server.application.*

public interface AuthProvider {

    public val name: String?

    public fun roles(principal: Any): Set<String> = emptySet()

    public fun skip(call: ApplicationCall): Boolean = false
}
