package ai.tech.core.misc.plugin.graphql

import com.apurebase.kgraphql.GraphQL
import ai.tech.core.misc.plugin.graphql.model.config.GraphQLConfig
import io.ktor.server.application.*

public fun Application.configureGraphQL(config: GraphQLConfig?) = config?.takeIf { it.enable != false }?.let {
    install(GraphQL) {
        it.playground?.let { playground = it }
        it.endpoint?.let { endpoint = it }
    }
}
