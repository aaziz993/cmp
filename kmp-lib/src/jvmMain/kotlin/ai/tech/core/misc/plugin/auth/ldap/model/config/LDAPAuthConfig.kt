package ai.tech.core.misc.plugin.auth.ldap.model.config

import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LDAPAuthConfig(
    val realm: String? = null,
    val charset: String? = null,
    val ldapServerURL: String,
    val userDNFormat: String,
    override val cookie: CookieConfig? = null,
    override val enable: Boolean = true
) : AuthProviderConfig
