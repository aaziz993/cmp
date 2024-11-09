package ai.tech.core.misc.plugin.auth.basic

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.cryptography.encodeDER
import ai.tech.core.misc.cryptography.hasher
import ai.tech.core.misc.cryptography.model.HashAlgorithm
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import ai.tech.core.misc.plugin.auth.basic.model.config.DigestConfig
import ai.tech.core.misc.plugin.auth.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.model.RoleEntity
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.util.*
import kotlinx.coroutines.flow.singleOrNull
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet

public class BasicAuthService(
    override val name: String,
    public val config: BasicAuthConfig,
    public val principalRepository: CRUDRepository<PrincipalEntity>?,
    public val roleRepository: CRUDRepository<RoleEntity>?,
    public val userTable: Map<String, String> = emptyMap(),
) : AuthProvider, ValidateAuthProvider<UserPasswordCredential> {

    private val digester: (String) -> ByteArray =
        config.digest?.takeIf(EnabledConfig::enable)?.let(DigestConfig::algorithm)?.let(::getDigester)
            ?: String::toByteArray

    private val userHashedTableAuth: UserHashedTableAuth = UserHashedTableAuth(
        digester,
        userTable.mapValues { (_, v) -> v.toByteArray() },
    )

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? = principalRepository?.transactional {
        val principal = find(predicate = "username".f.eq(credential.name)).singleOrNull()

        if (principal == null || digester(credential.password) != principal.password.toByteArray()) {
            return@transactional null
        }

        User(username = credential.name, roles = roleRepository?.let { it.find(predicate = "userId".f.eq(principal.id)).map { it.name }.toSet().ifEmpty { null } })
    } ?: userHashedTableAuth.authenticate(credential)?.let(UserIdPrincipal::name)?.let(::User)

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}

private fun getDigester(algorithm: String): (String) -> ByteArray {
    val digester = MessageDigest.getInstance(algorithm)
    return { digester.digest(it.toByteArray()) }
}
