package ai.tech.core.misc.plugin.auth.digest

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.DigestAuthProvider
import ai.tech.core.misc.plugin.auth.StoreAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.database.AuthRepository
import ai.tech.core.misc.plugin.auth.digest.model.config.DigestAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*

public class DigestAuthService(
    override val name: String,
    override val config: DigestAuthConfig,
    override val getRepository: (provider: String, database: String?, userTable: String?, roleTable: String?) -> AuthRepository?,
) : AuthProvider, DigestAuthProvider, StoreAuthProvider, ValidateAuthProvider<DigestCredential> {

    private val userTable = getUserTable()

    private val repository = getRepository()

    override suspend fun digestProvider(userName: String, realm: String): ByteArray? =
        userTable[userName] ?: repository?.getPrincipal(userName)?.password?.let(String::toByteArray)

    override suspend fun validate(call: ApplicationCall, credential: DigestCredential): Any? =
        if (userTable.contains(credential.userName)) {
            User(credential.userName)
        }
        else {
            repository?.getUserPassword(credential.userName)?.first
        }

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}
