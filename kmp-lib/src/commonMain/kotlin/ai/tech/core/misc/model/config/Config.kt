package ai.tech.core.misc.model.config

import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.server.KtorServerConfig

public interface Config {

    public val project: String

    public val consul: ConsulConfig

    public val localization: LocalizationConfig

    public val validator: ValidatorConfig

    public val ktor: KtorServerConfig
}
