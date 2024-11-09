package ai.tech.core.misc.plugin.auth.digest

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.digest.model.config.DigestAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import kotlinx.coroutines.flow.singleOrNull

public class DigestAuthService(
    override val name: String,
    public val config: DigestAuthConfig,
    public val userRepository: CRUDRepository<User>,
    public val roleRepository: CRUDRepository<User>,
) : AuthProvider, ValidateAuthProvider<DigestCredential> {

    override suspend fun validate(call: ApplicationCall, credential: DigestCredential): Any? = userRepository.transactional {
        val user = find(predicate = "username".f.eq(credential.userName)).singleOrNull()



        if (credential.userName.isNotEmpty()) {
            CustomPrincipal(credential.userName, credential.realm)
        }
        else {
            null
        }
    }

    override fun roles(principal: Any): Set<String> = principal.roles.orEmpty()
}
