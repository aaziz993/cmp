package ai.tech.core.misc.plugin.swagger

import ai.tech.core.misc.plugin.swagger.model.config.SwaggerConfig
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.PluginConfigDsl
import io.ktor.server.application.*

public fun Application.configureSwagger(config: SwaggerConfig?, block: (PluginConfigDsl.() -> Unit)? = null) {
    val configBlock: (PluginConfigDsl.() -> Unit)? = config?.takeIf { it.enable != false }?.let {
        {
            swagger {
                it.forwardRoot?.let { forwardRoot = it }
                it.swaggerUrl?.let { swaggerUrl = it }
                it.rootHostPath?.let { rootHostPath = it }
                it.authentication?.let { authentication = it }
            }
            it.info?.let {
                info {
                    it.title?.let { title = it }
                    it.version?.let { version = it }
                    it.description?.let { description = it }
                    it.termsOfService?.let { termsOfService = it }
                    it.contact?.let {
                        contact {
                            it.name?.let { name = it }
                            it.url?.let { url = it }
                            it.email?.let { email = it }
                        }
                    }
                    it.license?.let {
                        license {
                            it.name?.let { name = it }
                            it.url?.let { url = it }
                        }
                    }
                }
            }
            it.securityScheme?.forEach {
                // We can add security
                securityScheme(it.key) {
                    it.value.type?.let { type = it }
                    it.value.name?.let { name = it }
                    it.value.location?.let { location = it }
                    it.value.scheme?.let { scheme = it }
                    it.value.bearerFormat?.let { bearerFormat = it }
                    it.value.openIdConnectUrl?.let { openIdConnectUrl = it }
                    it.value.description?.let { description = it }
                }
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    // https://github.com/SMILEY4/ktor-swagger-ui/wiki/Configuration
    // http://xxx/swagger/
    install(SwaggerUI) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
