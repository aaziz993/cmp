package ai.tech.core.misc.plugin.session.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class SessionCookieConfig(
    val name: String,
    val cookie: CookieConfig? = null,
)
