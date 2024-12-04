package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.jwt.model.JWTConfig
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import ai.tech.core.misc.type.multiple.iterable.contains
import ai.tech.core.misc.type.multiple.iterable.containsAll
import ai.tech.core.misc.type.multiple.iterable.containsNone
import ai.tech.core.misc.type.single.toLocalDateTime
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import kotlinx.datetime.toJavaLocalDateTime

public abstract class AbstractJWTAuthService<out T : JWTConfig>(override val name: String?, public val config: T) :
    AuthProvider,
    ValidateAuthProvider<JWTCredential>,
    ChallengeAuthProvider {

    override suspend fun validate(call: ApplicationCall, credential: JWTCredential): JWTPrincipal? = with(config) {
        if (
            issuers?.contains(credential.payload.issuer) != false &&

            subjects?.contains(credential.payload.subject) != false &&

            audiences?.let { credential.payload.audience.contains(it, audienceResolution) } != false &&

            jwtId?.let { credential.jwtId!! == it } != false &&

            claimsPresence?.let(credential.payload.claims.keys::containsAll) != false &&

            nullClaims?.let {
                credential.payload.claims.filterValues(Claim::isNull).keys.containsAll(it)
            } != false &&

            claim?.all { (name, value) ->

                val claim = credential.payload.getClaim(name)

                (claim.asKotlinLocalDateTime() ?: claim.asString()) == value
            } != false &&

            arrayClaim?.all { (name, value) ->
                val arrayClaim = credential.payload.getClaim(name).asArray<Any?>()?.filterNotNull()?.map { value ->
                    if (value is Claim) {
                        return@map value.asKotlinLocalDateTime() ?: value.asString()
                    }
                    value
                }.orEmpty()
                arrayClaim.containsAll(value)
            } != false &&

            config.issuedAt?.let { credential.issuedAt!!.toLocalDateTime() == it.toJavaLocalDateTime() } != false &&

            config.expiresAfter?.let { credential.expiresAt!!.toLocalDateTime().isAfter(it.toJavaLocalDateTime()) } != false
        ) {
            credential.payload.getClaim<Claim?>(*config.usernameClaim.toTypedArray())?.asString()?.let {
                JWTPrincipal(credential.payload)
            }
        }
        else {
            null
        }
    }

    override fun roles(principal: Any): Set<String> = (principal as JWTPrincipal).payload.roles

    private val Payload.roles: Set<String>
        get() = config.rolesClaim?.let { getClaim<ArrayList<String>?>(*it.toTypedArray())?.toSet() }
            .orEmpty()
}

