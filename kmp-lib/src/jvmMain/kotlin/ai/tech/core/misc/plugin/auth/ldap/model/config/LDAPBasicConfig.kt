package ai.tech.core.misc.plugin.auth.ldap.model.config

import ai.tech.core.misc.plugin.auth.basic.model.config.BaseBasicAuthConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.RealmAuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LDAPBasicConfig(
    override val realm: String? = null,
    override val charset: String? = null,
    override val ldapServerURL: String,
    override val userDNFormat: String,
    override val cookie: CookieConfig? = null,
    override val enabled: Boolean = true
) : AuthProviderConfig, LDAPAuthConfig, BaseBasicAuthConfig, RealmAuthProviderConfig
