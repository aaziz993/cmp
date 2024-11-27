package ai.tech.misc.auth.firebase.server

import ai.tech.core.misc.auth.firebase.server.FIREBASE_AUTH
import ai.tech.core.misc.auth.firebase.server.FirebaseJWTAuthKey
import io.ktor.server.auth.*
import io.ktor.server.testing.*

public class FirebaseAuthTestProvider(config: FirebaseTestConfig) : AuthenticationProvider(config) {

    private val authFunction: () -> Any? = config.mockAuthProvider

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val mockUser: Any? = authFunction()
        if (mockUser != null) {
            context.principal(mockUser)
        }
        else {
            context.error(
                FirebaseJWTAuthKey,
                AuthenticationFailedCause.Error("User was mocked to be unauthenticated"),
            )
        }
    }
}

public class FirebaseTestConfig(name: String?) : AuthenticationProvider.Config(name) {

    public var mockAuthProvider: () -> Any? = { null }
}

public fun ApplicationTestBuilder.mockAuthentication(mockAuth: () -> Any?) {
    install(Authentication) {
        val provider = FirebaseAuthTestProvider(
            FirebaseTestConfig(FIREBASE_AUTH).apply {
                mockAuthProvider = mockAuth
            },
        )
        register(provider)
    }
}
