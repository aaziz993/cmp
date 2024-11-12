package ai.tech.core.misc.plugin.auth.database

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.database.principal.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.database.role.model.RoleEntity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import kotlin.collections.ifEmpty

public class AuthRepository(
    private val principalRepository: CRUDRepository<PrincipalEntity>,
    private val roleRepository: CRUDRepository<RoleEntity>? = null
) {
    public suspend fun getPrincipal(username: String): PrincipalEntity? = principalRepository.transactional {
        find(predicate = "username".f.eq(username)).singleOrNull()
    }


    public suspend fun getUserPassword(username: String): Pair<User, String>? = principalRepository.transactional {
        getPrincipal(username)?.let { principal ->

            User(
                principal.username,
                roles = roleRepository?.let {
                    it.find(predicate = "userId".f.eq(principal.id)).map { it.name }.toSet().ifEmpty { null }
                }) to principal.password
        }
    }
}
