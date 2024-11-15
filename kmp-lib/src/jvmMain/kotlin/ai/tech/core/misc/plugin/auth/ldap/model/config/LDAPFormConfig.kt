package ai.tech.core.misc.plugin.auth.ldap.model.config

import ai.tech.core.misc.plugin.auth.form.model.config.BaseFormAuthConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LDAPFormConfig(
    override val userParamName: String? = null,
    override val passwordParamName: String? = null,
    override val ldapServerURL: String,
    override val userDNFormat: String,
    override val cookie: CookieConfig? = null,
    override val enable: Boolean = true
) : AuthProviderConfig, LDAPAuthConfig, BaseFormAuthConfig
