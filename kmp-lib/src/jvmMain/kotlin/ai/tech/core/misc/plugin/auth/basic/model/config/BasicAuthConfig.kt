package ai.tech.core.misc.plugin.auth.basic.model.config

import ai.tech.core.misc.plugin.auth.model.config.DigestAuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.DigestConfig
import ai.tech.core.misc.plugin.auth.model.config.StoreAuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class BasicAuthConfig(
    val realm: String? = null,
    val charset: String? = null,
    override val digest: DigestConfig? = null,
    override val file: List<String>? = null,
    override val database: String,
    override val principalTable: String,
    override val roleTable: String? = null,
    override val cookie: CookieConfig? = null,
    override val enable: Boolean = true
) : DigestAuthProviderConfig, StoreAuthProviderConfig
