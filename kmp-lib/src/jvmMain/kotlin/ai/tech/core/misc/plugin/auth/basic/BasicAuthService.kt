package ai.tech.core.misc.plugin.auth.basic

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.data.filesystem.pathExtension
import ai.tech.core.data.filesystem.readResourceBytes
import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import ai.tech.core.misc.plugin.auth.basic.model.config.DigestConfig
import ai.tech.core.misc.plugin.auth.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.model.RoleEntity
import ai.tech.core.misc.type.decodeAnyFromString
import ai.tech.core.misc.type.multiple.decode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import kotlinx.serialization.json.Json
import java.util.Properties

public class BasicAuthService(
    override val name: String,
    public val config: BasicAuthConfig,
    public val principalRepository: CRUDRepository<PrincipalEntity>?,
    public val roleRepository: CRUDRepository<RoleEntity>?,
) : AuthProvider, ValidateAuthProvider<UserPasswordCredential> {

    private val digester: (String) -> ByteArray =
        config.digest?.takeIf(EnabledConfig::enable)?.let(DigestConfig::algorithm)?.let(::getDigester)
            ?: String::toByteArray

    private val userHashedTableAuth: UserHashedTableAuth = UserHashedTableAuth(
        digester,
        config.file?.fold(emptyMap<String, ByteArray>()) { acc, file ->
            acc + readResourceBytes(file)?.let { bytes ->
                when (file.pathExtension) {
                    "json" -> Json.Default.decodeAnyFromString(bytes.decodeToString()) as Map<String, String>

                    "properties" -> Properties().apply {
                        load(ByteArrayInputStream(bytes))
                    }.entries.associate { (k, v) -> k.toString() to v.toString() }

                    else -> throw IllegalArgumentException("Unsupported file format: $file")
                }.mapValues { (_, v) -> v.toByteArray() }
            }.orEmpty()
        }.orEmpty(),
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
