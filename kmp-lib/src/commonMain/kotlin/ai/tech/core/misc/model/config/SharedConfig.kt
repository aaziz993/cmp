package ai.tech.core.misc.model.config

import ai.tech.core.data.validator.model.UserValidatorConfig
import ai.tech.core.misc.model.config.server.KtorServerConfig
import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.config.LocalizationConfig

public interface SharedConfig {

    public val projectName: String

    public val environmentName: String?

    public val localization: LocalizationConfig

    public val localizationProvider: String?

    public val language: Language

    public val userValidator: UserValidatorConfig

    public val database: DatabaseConfig?

    public val ktor: KtorServerConfig
}
