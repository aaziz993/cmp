package ai.tech.core.misc.plugin.auth.ldap

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.DigestAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.ldap.model.config.LDAPAuthConfig
import com.sun.jndi.ldap.LdapClient
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.server.auth.ldap.*
import javax.naming.Context
import javax.naming.directory.InitialDirContext
import javax.naming.ldap.LdapContext

public open class LDAPDigestAuthService(
    override val name: String?,
    public val config: LDAPAuthConfig,
) : AuthProvider, DigestAuthProvider, ValidateAuthProvider<DigestCredential> {

    override suspend fun digestProvider(userName: String, realm: String): ByteArray? {

        ldapAuthenticate(cred, "ldap://100.20.1.1:389", "OU=TestCatalog,DC=op,DC=serv") {
            val users = (lookup("OU=UsersCatalog") as LdapContext)
//            logger.debug { users }
//            val controls = SearchControls().apply {
//                searchScope = SearchControls.ONELEVEL_SCOPE
//                returningAttributes = arrayOf("+", "*")
//            }
//
//            users.search(cred.name, "", controls).asSequence().firstOrNull {
//                val ldapPassword = (it.attributes.get("userPassword")?.get() as ByteArray?)?.toString(Charsets.UTF_8)
//                ldapPassword == cred.password
//            }?.let { UserIdPrincipal(cred.name) }
        }
    }

    override suspend fun validate(
        call: ApplicationCall,
        credential: DigestCredential,
    ): User? {


        val ldapServerURL = "ldap://ldap.example.com" // Replace with your LDAP server URL
        val userDNFormat = "uid=%s,ou=users,dc=example,dc=com" // Update as per your LDAP schema
        val userDN = userDNFormat.format(credential.userName)

        // Use LDAP to verify the user's identity
         try {
            val env = mutableMapOf<String, Any>(
                Context.INITIAL_CONTEXT_FACTORY to "com.sun.jndi.ldap.LdapCtxFactory",
                Context.PROVIDER_URL to ldapServerURL,
                Context.SECURITY_AUTHENTICATION to "simple",
                Context.SECURITY_PRINCIPAL to userDN,
                Context.SECURITY_CREDENTIALS to credential.digestPassword // Digest password sent by the client
            )

             ldapAuthenticate(credential,config.ldapServerURL,env){

             }

        } catch (e: Exception) {
            false // LDAP authentication failed
        }
    }

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}
