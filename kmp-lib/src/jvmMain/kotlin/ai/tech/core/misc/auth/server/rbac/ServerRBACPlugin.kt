package ai.tech.core.misc.auth.server.rbac

import ai.tech.core.misc.auth.model.Role
import ai.tech.core.misc.auth.model.exception.UnauthorizedAccessException
import ai.tech.core.misc.auth.server.rbac.model.ServerRBACPluginConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val pluginGlobalConfig: MutableMap<String?, ServerRBACPluginConfig> = mutableMapOf()

public fun rbac(name: String? = null, config: ServerRBACPluginConfig.() -> Unit) {
    pluginGlobalConfig += name to ServerRBACPluginConfig().apply(config)
}

public class RBACConfiguration {

    public lateinit var configurations: Set<String?>
    public lateinit var role: Role
}

public val RBACPlugin: RouteScopedPlugin<RBACConfiguration> =
    createRouteScopedPlugin("RoleBasedAuthorization", ::RBACConfiguration) {
        require(pluginConfig.configurations.any { it in pluginGlobalConfig.keys }) {
            error("RBACPlugin doesn't contain any of the configurations ${pluginConfig.configurations.joinToString(", ")}")
        }

        with(pluginConfig) {

            val globalPluginConfigs = configurations.map { pluginGlobalConfig[it]!! }

            on(AuthenticationChecked) { call ->
                call.principal<Any>()?.let { principal ->

                    val roles = globalPluginConfigs.flatMap { it.roleExtractor(principal) }.toSet()

                    if (!role.validate(roles)) {
                        val message =
                            "Authorization failed for ${call.request.path()} should be ${role.resolution.name.lowercase()} of the roles [${
                                roles.joinToString(", ")
                            }] in [${role.roles.joinToString(", ")}]"
                        if (application.developmentMode) {
                            application.log.warn(message)
                        }
                        if (globalPluginConfigs.any { it.throwException != false }) {
                            throw UnauthorizedAccessException(message)
                        }
                        else {
                            call.respond(HttpStatusCode.Forbidden)
                        }
                    }
                }
            }
        }
    }

public class AuthorizedRouteSelector(private vararg val configurations: String?) : RouteSelector() {

    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
        RouteSelectorEvaluation.Constant

    override fun toString(): String = "(authorize $configurations)"
}
