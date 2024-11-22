package ai.tech.core.misc.model.config

import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.location.localization.model.config.LocalizationConfig
import ai.tech.core.misc.model.config.client.HttpClientConfig
import ai.tech.core.misc.model.config.presentation.PresentationConfig
import ai.tech.core.misc.model.config.server.SharedHostConfig

public interface Config {

    public val application: ApplicationConfig

    public val httpClient: HttpClientConfig

    public val consul: ConsulConfig?

    public val localization: LocalizationConfig

    public val validator: ValidatorConfig

    public val presentation: PresentationConfig

    public val host: SharedHostConfig
}
