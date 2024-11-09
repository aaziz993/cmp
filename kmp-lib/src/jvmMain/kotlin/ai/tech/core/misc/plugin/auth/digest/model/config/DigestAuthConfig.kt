package ai.tech.core.misc.plugin.auth.digest.model.config

import ai.tech.core.misc.plugin.auth.model.config.DatabaseAuthConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DigestAuthConfig(
    val realm: String? = null,
    val algorithmName: String? = null,
    override val database: String,
    override val cookie: CookieConfig? = null,
    override val enable: Boolean = true
) : DatabaseAuthConfig
