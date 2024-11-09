package ai.tech.core.misc.plugin.auth.form.model.config

import ai.tech.core.misc.plugin.auth.model.config.DatabaseAuthConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class FormAuthConfig(
    val userParamName: String? = null,
    val passwordParamName: String? = null,
    override val database: String,
    override val cookie: CookieConfig? = null,
    override val enable: Boolean = true
) : DatabaseAuthConfig
