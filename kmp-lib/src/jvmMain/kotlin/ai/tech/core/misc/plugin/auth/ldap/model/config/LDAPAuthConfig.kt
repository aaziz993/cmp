package ai.tech.core.misc.plugin.auth.ldap.model.config

import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

public interface LDAPAuthConfig {

    public val ldapServerURL: String
    public val userDNFormat: String
}
