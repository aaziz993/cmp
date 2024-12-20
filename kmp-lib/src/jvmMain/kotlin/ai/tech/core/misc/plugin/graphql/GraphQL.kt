package ai.tech.core.misc.plugin.graphql

import ai.tech.core.misc.model.config.EnabledConfig
import com.apurebase.kgraphql.GraphQL
import ai.tech.core.misc.plugin.graphql.model.config.GraphQLConfig
import io.ktor.server.application.*

public fun Application.configureGraphQL(config: GraphQLConfig?, block: (GraphQL.Configuration.() -> Unit)? = null) {
    val configBlock: (GraphQL.Configuration.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.playground?.let { playground = it }
            it.endpoint?.let { endpoint = it }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(GraphQL) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
