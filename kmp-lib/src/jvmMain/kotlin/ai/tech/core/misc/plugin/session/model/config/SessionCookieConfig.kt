package ai.tech.core.misc.plugin.session.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SessionCookieConfig(
    val name: String,
    val cookie: CookieConfig? = null,
    override val enable: Boolean = true,
) : EnabledConfig
