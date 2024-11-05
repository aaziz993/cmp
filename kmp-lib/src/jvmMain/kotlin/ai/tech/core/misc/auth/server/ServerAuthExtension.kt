package ai.tech.core.misc.auth.server

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.auth.server.rbac.AuthorizedRouteSelector
import ai.tech.core.misc.auth.server.rbac.RBACPlugin
import io.ktor.server.auth.*
import io.ktor.server.routing.*

public fun Route.auth(
    auth: AuthResource?,
    optional: Boolean = false,
    build: Route.() -> Unit
): Route =
    if (auth == null) {
        build().let {
            this
        }
    } else {
        authenticate(*auth.providers.toTypedArray(), optional = optional) {
            auth.role?.let {
                createChild(AuthorizedRouteSelector(*auth.providers.toTypedArray())).apply {
                    install(RBACPlugin) {
                        this.configurations = auth.providers.toSet()
                        this.role = it
                    }
                    build()
                }
            } ?: build()
        }
    }
