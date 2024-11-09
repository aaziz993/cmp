package ai.tech.core.misc.plugin.auth.ldap

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.ldap.model.config.LDAPAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.server.auth.ldap.*

public class LDAPAuthService(
    override val name: String,
    public val config: LDAPAuthConfig,
) : AuthProvider, ValidateAuthProvider<UserPasswordCredential> {

    override suspend fun validate(
        call: ApplicationCall,
        credential: UserPasswordCredential,
    ): User? =
        ldapAuthenticate(credential, config.ldapServerURL, config.userDNFormat)

    override fun roles(principal: Any): Set<String> = principal.roles.orEmpty()
}
