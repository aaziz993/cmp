package ai.tech.core.misc.model.config

import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.server.KtorServerConfig

public interface Config {

    public val project: String

    public val localization: LocalizationConfig

    public val localizationProvider: String?

    public val language: Language

    public val validator: ValidatorConfig

    public val database: Map<String, DatabaseProviderConfig>?

    public val ktor: KtorServerConfig
}
