package ai.tech.core.misc.plugin.auth.form

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.AbstractChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.form.model.config.FormAuthConfig
import ai.tech.core.misc.plugin.auth.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.model.RoleEntity
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import kotlin.collections.ifEmpty
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet

public class FormAuthService(
    override val name: String,
    public val config: FormAuthConfig,
    public val principalRepository: CRUDRepository<PrincipalEntity>,
    public val roleRepository: CRUDRepository<RoleEntity>?,
) : AuthProvider, ValidateAuthProvider<UserPasswordCredential>, AbstractChallengeAuthProvider(config) {

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? = principalRepository.transactional {
        val principal = find(predicate = "username".f.eq(credential.name)).singleOrNull()

        if (principal == null || credential.password != principal.password) {
            return@transactional null
        }

        User(username = credential.name, roles = roleRepository?.let { it.find(predicate = "userId".f.eq(principal.id)).map { it.name }.toSet().ifEmpty { null } })
    }

    override fun roles(principal: Any): Set<String> = (principal as User).roles ?: emptySet()
}
