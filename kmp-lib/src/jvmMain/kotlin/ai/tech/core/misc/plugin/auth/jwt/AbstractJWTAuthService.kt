package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.auth.model.exception.UnauthenticatedAccessException
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.jwt.model.JWTConfig
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import ai.tech.core.misc.plugin.session.model.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import java.util.*

public abstract class AbstractJWTAuthService<out T : JWTConfig>(public override val name: String, public val config: T) :
    AuthProvider,
    ValidateAuthProvider<JWTCredential>,
    ChallengeAuthProvider {

    override suspend fun validate(call: ApplicationCall, credential: JWTCredential): JWTPrincipal? =
        if (
            credential.payload.audience.contains(config.audience) &&
            credential.payload.issuer == config.issuer &&
            credential.expiresAt?.after(Date()) != false
        ) {
            credential.payload.getClaim<Claim?>(*config.usernameClaimKeys.toTypedArray())?.asString()?.let {
                call.sessions.set(
                    name,
                    UserSession(
                        it,
                        credential.payload.roles,
                        1,
                    ),
                )
                JWTPrincipal(credential.payload)
            }
        }
        else {
            null
        }

    override fun roles(principal: Any): Set<String> = (principal as JWTPrincipal).payload.roles

    override suspend fun challenge(call: ApplicationCall, vararg args: Any) {
        val message = "Token is not valid or has expired"

        if (config.throwException != false) {
            throw UnauthenticatedAccessException(message)
        }

        call.respond(HttpStatusCode.Unauthorized, message)
    }

    private val Payload.roles: Set<String>
        get() = getClaim<ArrayList<String>?>(*config.rolesClaimKeys.toTypedArray())?.toSet()
            .orEmpty()
}
