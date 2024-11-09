package ai.tech.core.misc.plugin.auth.basic

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.util.*
import kotlinx.coroutines.flow.singleOrNull
import java.nio.charset.Charset

public class BasicAuthService(
    override val name: String,
    public val config: BasicAuthConfig,
    public val userRepository: CRUDRepository<User>,
    public val roleRepository: CRUDRepository<User>,
    private val digestFunction: ((String) -> ByteArray)? = config.digestFunction?.let { cfg -> getDigestFunction(cfg.algorithm) { cfg.salt } }
) : AuthProvider, ValidateAuthProvider<UserPasswordCredential> {

    private val passwordsEquals: (String, String) -> Boolean = if (digestFunction == null) {
        { p1, p2 -> p1 == p2 }
    }
    else {
        { p1, p2 -> digestFunction.invoke(p1) contentEquals p2.toByteArray(Charset.forName(config.charset)) }
    }

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): User? = userRepository.transactional {
        val user = find(predicate = "username".f.eq(credential.name)).singleOrNull()

        if (user == null || !passwordsEquals(credential.password, user.password)) {
            return@transactional null
        }

        roleRepository.find("userId".f.eq(user.id)).map { UserIdPrincipalMetadata(credential.name, it) }.get()
    }

    override fun roles(principal: Any): Set<String> = principal.roles.orEmpty()
}
