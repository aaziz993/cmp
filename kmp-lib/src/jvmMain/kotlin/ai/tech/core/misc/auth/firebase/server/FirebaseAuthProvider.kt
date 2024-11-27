package ai.tech.core.misc.auth.firebase.server

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public class FirebaseAuthProvider(config: FirebaseConfig) : AuthenticationProvider(config) {

    public val authHeader: (ApplicationCall) -> HttpAuthHeader? = config.authHeader
    private val authFunction = config.firebaseAuthenticationFunction

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val token = authHeader(context.call)

        if (token == null) {
            context.challenge(FirebaseJWTAuthKey, AuthenticationFailedCause.InvalidCredentials) { challengeFunc, call ->
                challengeFunc.complete()
                call.respond(UnauthorizedResponse(HttpAuthHeader.bearerAuthChallenge(realm = "firebaseAuth")))
            }
            return
        }

        try {
            val principal = verifyFirebaseIdToken(context.call, token, authFunction)

            if (principal != null) {
                context.principal(principal)
            }
        }
        catch (cause: Throwable) {
            val message = cause.message ?: cause.javaClass.simpleName
            context.error(FirebaseJWTAuthKey, AuthenticationFailedCause.Error(message))
        }
    }
}

public class FirebaseConfig(name: String?) : AuthenticationProvider.Config(name) {

    internal var authHeader: (ApplicationCall) -> HttpAuthHeader? =
        { call -> call.request.parseAuthorizationHeaderOrNull() }

    public var firebaseAuthenticationFunction: AuthenticationFunction<FirebaseToken> = {
        throw NotImplementedError(FirebaseImplementationError)
    }

    public fun validate(validate: suspend ApplicationCall.(FirebaseToken) -> Any?) {
        firebaseAuthenticationFunction = validate
    }
}

public fun AuthenticationConfig.firebase(name: String? = null, configure: FirebaseConfig.() -> Unit) {
    val provider = FirebaseAuthProvider(FirebaseConfig(name).apply(configure))
    register(provider)
}

public suspend fun verifyFirebaseIdToken(
    call: ApplicationCall,
    authHeader: HttpAuthHeader,
    tokenData: suspend ApplicationCall.(FirebaseToken) -> Any?
): Any? {
    val token: FirebaseToken = try {
        if (authHeader.authScheme == "Bearer" && authHeader is HttpAuthHeader.Single) {
            withContext(Dispatchers.IO) {
                FirebaseAuth.getInstance().verifyIdToken(authHeader.blob)
            }
        }
        else {
            null
        }
    }
    catch (ex: Exception) {
        ex.printStackTrace()
        return null
    } ?: return null

    return tokenData(call, token)
}

private fun HttpAuthHeader.Companion.bearerAuthChallenge(realm: String): HttpAuthHeader {
    return HttpAuthHeader.Parameterized("Bearer", mapOf(HttpAuthHeader.Parameters.Realm to realm))
}

private fun ApplicationRequest.parseAuthorizationHeaderOrNull() = try {
    parseAuthorizationHeader()
}
catch (_: IllegalArgumentException) {
    println("failed to parse token")
    null
}

private const val FirebaseJWTAuthKey: String = "FirebaseAuth"

private const val FirebaseImplementationError =
    "Firebase  auth validate function is not specified, use firebase { { ... } }to fix"
