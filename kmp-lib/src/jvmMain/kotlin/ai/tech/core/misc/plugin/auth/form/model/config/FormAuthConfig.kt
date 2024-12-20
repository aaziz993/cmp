package ai.tech.core.misc.plugin.auth.form.model.config

import ai.tech.core.misc.plugin.auth.model.config.AuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.ChallengeAuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.DigestAuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.DigestConfig
import ai.tech.core.misc.plugin.auth.model.config.StoreAuthProviderConfig
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import kotlinx.serialization.Serializable

@Serializable
public data class FormAuthConfig(
    override val userParamName: String? = null,
    override val passwordParamName: String? = null,
    override val digest: DigestConfig? = null,
    override val file: List<String>? = null,
    override val database: String,
    override val principalTable: String,
    override val roleTable: String? = null,
    override val cookie: CookieConfig? = null,
    override val exception: Boolean = false,
    override val enabled: Boolean = true
) : AuthProviderConfig, BaseFormAuthConfig, DigestAuthProviderConfig, StoreAuthProviderConfig, ChallengeAuthProviderConfig
