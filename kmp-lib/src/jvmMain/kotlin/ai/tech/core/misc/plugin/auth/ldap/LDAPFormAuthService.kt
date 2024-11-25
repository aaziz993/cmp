package ai.tech.core.misc.plugin.auth.ldap

import ai.tech.core.misc.plugin.auth.ChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.ldap.model.config.LDAPAuthConfig

public open class LDAPFormAuthService(
    name: String?,
    config: LDAPAuthConfig,
) : LDAPAuthService(name, config), ChallengeAuthProvider
