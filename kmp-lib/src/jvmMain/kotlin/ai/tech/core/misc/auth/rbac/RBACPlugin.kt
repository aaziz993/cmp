package ai.tech.core.misc.auth.rbac

import ai.tech.core.misc.auth.model.exception.UnauthorizedAccessException
import ai.tech.core.misc.auth.rbac.model.RBACPluginConfig
import ai.tech.core.misc.type.multiple.iterable.contains
import ai.tech.core.misc.type.multiple.iterable.model.ContainResolution
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val pluginGlobalConfig: MutableMap<String?, RBACPluginConfig> = mutableMapOf()

public fun rbac(name: String? = null, config: RBACPluginConfig.() -> Unit) {
    pluginGlobalConfig += name to RBACPluginConfig().apply(config)
}

public class RBACConfiguration {

    public lateinit var configurations: Set<String?>
    public lateinit var roles: Set<String>
    public lateinit var roleResolution: ContainResolution
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

                    val principalRoles = globalPluginConfigs.flatMap { it.roleExtractor(principal) }.toSet()

                    if (!roles.contains(principalRoles, roleResolution)) {
                        val message =
                            "Authorization failed for ${call.request.path()} should be ${roleResolution.name.lowercase()} of the required roles [${
                                roles.joinToString()
                            }] in principal roles [${principalRoles.joinToString()}]"
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
