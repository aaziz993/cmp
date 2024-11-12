package ai.tech.core.misc.plugin.auth.basic

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.DigestAuthProvider
import ai.tech.core.misc.plugin.auth.StoreAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import ai.tech.core.misc.plugin.auth.database.AuthRepository
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import kotlinx.serialization.InternalSerializationApi

public class BasicAuthService(
    override val name: String,
    override val config: BasicAuthConfig,
    override val getRepository: (provider: String, database: String?, userTable: String?, roleTable: String?) -> AuthRepository?,
) : AuthProvider,
    DigestAuthProvider,
    StoreAuthProvider,
    ValidateAuthProvider<UserPasswordCredential> {

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private val userHashedTableAuth: UserHashedTableAuth = UserHashedTableAuth(getDigester(), getUserTable())

    private val repository = getRepository()

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? =
        userHashedTableAuth.authenticate(credential)?.let(UserIdPrincipal::name)?.let(::User)
            ?: repository?.getUserPassword(credential.name)?.takeIf { (_, password) ->
                userHashedTableAuth.digester(credential.password) == password.toByteArray()
            }

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}
