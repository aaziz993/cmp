package ai.tech.core.misc.plugin.auth.model.config

import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import ai.tech.core.misc.plugin.auth.digest.model.config.DigestAuthConfig
import ai.tech.core.misc.plugin.auth.form.model.config.FormAuthConfig
import ai.tech.core.misc.plugin.auth.jwt.model.JWTHS256Config
import ai.tech.core.misc.plugin.auth.jwt.model.JWTRS256Config
import ai.tech.core.misc.plugin.auth.ldap.model.config.LDAPAuthConfig
import ai.tech.core.misc.plugin.auth.oauth.model.config.ServerOAuthConfig
import kotlinx.serialization.Serializable

@Serializable
public data class AuthConfig(
    val basic: Map<String, BasicAuthConfig> = emptyMap(),
    val digest: Map<String, DigestAuthConfig> = emptyMap(),
    val form: Map<String, FormAuthConfig> = emptyMap(),
    val ldap: Map<String, LDAPAuthConfig> = emptyMap(),
    val oauth: Map<String, ServerOAuthConfig> = emptyMap(),
    val jwtHs256: Map<String, JWTHS256Config> = emptyMap(),
    val jwtRs256: Map<String, JWTRS256Config> = emptyMap(),
)
