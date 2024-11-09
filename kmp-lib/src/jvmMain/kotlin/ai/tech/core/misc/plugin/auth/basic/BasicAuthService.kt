package ai.tech.core.misc.plugin.auth.basic

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import ai.tech.core.misc.plugin.auth.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.model.RoleEntity
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.util.*
import kotlinx.coroutines.flow.singleOrNull
import java.nio.charset.Charset
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet

public class BasicAuthService(
    override val name: String,
    public val config: BasicAuthConfig,
    public val principalRepository: CRUDRepository<PrincipalEntity>,
    public val roleRepository: CRUDRepository<RoleEntity>?,
    private val digestFunction: ((String) -> ByteArray)? = config.digestFunction?.let { cfg -> getDigestFunction(cfg.algorithm) { cfg.salt } }
) : AuthProvider, ValidateAuthProvider<UserPasswordCredential> {

    private val passwordsEquals: (String, String) -> Boolean = if (digestFunction == null) {
        { p1, p2 -> p1 == p2 }
    }
    else {
        { p1, p2 -> digestFunction.invoke(p1) contentEquals p2.toByteArray(Charset.forName(config.charset)) }
    }

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): User? = principalRepository.transactional {
        val principal = find(predicate = "username".f.eq(credential.name)).singleOrNull()

        if (principal == null || !passwordsEquals(credential.password, principal.password)) {
            return@transactional null
        }

        User(username = credential.name, roles = roleRepository?.let { it.find(predicate = "userId".f.eq(principal.id)).map { it.name }.toSet().ifEmpty { null } })
    }

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}
