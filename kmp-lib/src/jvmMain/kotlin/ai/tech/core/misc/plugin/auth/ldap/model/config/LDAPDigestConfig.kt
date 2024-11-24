package ai.tech.core.misc.plugin.auth.ldap.model.config

import ai.tech.core.misc.plugin.auth.digest.model.config.BaseDigestAuthConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.RealmAuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LDAPDigestConfig(
    override val realm: String?,
    override val algorithmName: String? = null,
    override val ldapServerURL: String,
    override val userDNFormat: String,
    override val cookie: CookieConfig? = null,
    override val enabled: Boolean = true
) : AuthProviderConfig, LDAPAuthConfig, BaseDigestAuthConfig, RealmAuthProviderConfig
