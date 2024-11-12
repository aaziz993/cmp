package ai.tech.core.misc.plugin.auth.basic

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.DigestAuthProvider
import ai.tech.core.misc.plugin.auth.PrincipalAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import ai.tech.core.misc.plugin.auth.basic.model.config.DigestConfig
import ai.tech.core.misc.plugin.auth.database.AuthRepository
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.util.getDigestFunction
import kotlinx.serialization.InternalSerializationApi

public class BasicAuthService(
    override val name: String,
    override val config: BasicAuthConfig,
    override val getRepository: (provider: String, database: String?, userTable: String?, roleTable: String?) -> AuthRepository?,
) : AuthProvider,
    DigestAuthProvider,
    PrincipalAuthProvider,
    ValidateAuthProvider<UserPasswordCredential> {

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private val userHashedTableAuth: UserHashedTableAuth = UserHashedTableAuth(digester, userTable)

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? =
        getRepository(name, config.database, config.principalTable, config.roleTable)?.getUserPassword(credential.name)?.takeIf { (_, password) ->
            digester(credential.password) == password.toByteArray()
        } ?: userHashedTableAuth.authenticate(credential)?.let(UserIdPrincipal::name)?.let(::User)

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}
