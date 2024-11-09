package ai.tech.core.misc.plugin.auth.basic.model.config

import ai.tech.core.misc.plugin.auth.model.config.DatabaseAuthConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class BasicAuthConfig(
    val realm: String? = null,
    val charset: String? = null,
    val digestFunction: DigestFunctionConfig? = null,
    override val database: String,
    override val userTable: String,
    override val cookie: CookieConfig? = null,
    override val throwException: Boolean = false,
    override val enable: Boolean = true
) : DatabaseAuthConfig
