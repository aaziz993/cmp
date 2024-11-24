package ai.tech.core.misc.plugin.graphql.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class GraphQLConfig(
    val playground: Boolean? = null,
    val endpoint: String? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
