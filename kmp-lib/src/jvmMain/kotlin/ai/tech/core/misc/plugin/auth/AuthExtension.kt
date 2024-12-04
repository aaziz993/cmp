package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.auth.rbac.AuthorizedRouteSelector
import ai.tech.core.misc.auth.rbac.RBACPlugin
import io.ktor.server.auth.*
import io.ktor.server.routing.*

public fun Route.authOpt(
    auth: AuthResource?,
    optional: Boolean = false,
    build: Route.() -> Unit
): Route =
    if (auth == null) {
        apply(build)
    }
    else {
        authenticate(*auth.providers.toTypedArray(), optional = optional) {
            if (auth.roles == null) {
                build()
            }
            else {
                createChild(AuthorizedRouteSelector(*auth.providers.toTypedArray())).apply {
                    install(RBACPlugin) {
                        configurations = auth.providers.toSet()
                        roles = auth.roles
                        roleResolution = auth.roleResolution
                    }
                    build()
                }
            }
        }
    }
