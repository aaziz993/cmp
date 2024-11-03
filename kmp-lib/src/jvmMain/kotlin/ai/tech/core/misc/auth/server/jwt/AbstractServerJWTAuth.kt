package ai.tech.core.misc.auth.server.jwt

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import ai.tech.core.misc.auth.server.ServerChallengeAuth
import ai.tech.core.misc.auth.server.ServerAuthProvider
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import ai.tech.core.misc.auth.server.jwt.model.ServerJWTConfig
import ai.tech.core.misc.plugin.session.model.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import java.util.*

public abstract class AbstractServerJWTAuth<out T : ServerJWTConfig>(public override val name: String, public val config: T) :
    ServerChallengeAuth,
    ServerAuthProvider<JWTPrincipal> {
    override fun roles(principal: JWTPrincipal): Set<String> = principal.payload.roles

    public fun validate(call: ApplicationCall, credential: JWTCredential): JWTPrincipal? =
        if (
            credential.payload.audience.contains(config.audience) &&
            credential.payload.issuer == config.issuer &&
            credential.expiresAt?.after(Date()) != false
        ) {
            credential.payload.getClaim<Claim?>(*config.usernameClaimKeys.toTypedArray())?.asString()?.let {
                call.sessions.set(
                    name, UserSession(
                        it,
                        credential.payload.roles,
                        1
                    )
                )
                JWTPrincipal(credential.payload)
            }
        } else {
            null
        }


    override suspend fun challenge(call: ApplicationCall, vararg args: Any): Unit =
        "Token is not valid or has expired".let {
            if (config.throwException != false) {
                throw UnauthenticatedAccessException(it)
            } else {
                call.respond(HttpStatusCode.Unauthorized, it)
            }
        }

    private val Payload.roles: Set<String>
        get() = getClaim<ArrayList<String>?>(*config.rolesClaimKeys.toTypedArray())?.toSet()
            ?: emptySet()
}
