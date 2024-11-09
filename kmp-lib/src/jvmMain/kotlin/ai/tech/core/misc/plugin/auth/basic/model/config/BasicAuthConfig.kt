package ai.tech.core.misc.plugin.auth.basic.model.config

import ai.tech.core.misc.plugin.auth.model.config.StorageAuthConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class BasicAuthConfig(
    val realm: String? = null,
    val charset: String? = null,
    val digest: DigestConfig? = null,
    override val database: String,
    override val principalTable: String,
    override val roleTable: String? = null,
    override val file: List<String>? = null,
    override val cookie: CookieConfig? = null,
    override val enable: Boolean = true
) : StorageAuthConfig
