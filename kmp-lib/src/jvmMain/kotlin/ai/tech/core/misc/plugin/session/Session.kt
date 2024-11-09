package ai.tech.core.misc.plugin.session

import ai.tech.core.misc.plugin.auth.model.model.config.ServerAuthConfig
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.jwt.model.JWTRS256Config
import ai.tech.core.misc.plugin.auth.model.oauth.model.config.ServerOAuthConfig
import ai.tech.core.misc.plugin.session.model.UserSession
import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import java.io.File

public fun Application.configureSession(config: ServerAuthConfig?, block: (SessionsConfig.() -> Unit)? = null) {
    val configBlock: (SessionsConfig.() -> Unit)? = config?.let {
        {
            it.jwtHs256.filterValues(EnabledConfig::enable).forEach { (name, config) ->
                cookie<UserSession>(name, config.cookie?.takeIf(EnabledConfig::enable))
            }

            it.jwtRs256.filterValues(EnabledConfig::enable).forEach { (name, config) ->
                cookie<UserSession>(name, JWTRS256Config.cookie?.takeIf(EnabledConfig::enable))
            }

            it.oauth.filterValues(EnabledConfig::enable).forEach { (name, config) ->
                cookie<UserSession>(name, ServerOAuthConfig.cookie?.takeIf(EnabledConfig::enable))
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(Sessions) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}

private inline fun <reified S : Any> SessionsConfig.cookie(
    name: String,
    config: CookieConfig?,
) = if (config == null) {
    cookie<S>(name)
}
else {
    val cookieBuilder: CookieSessionBuilder<S>.() -> Unit = {
        config.maxAgeInSeconds?.let { cookie.maxAgeInSeconds = it }
        config.encoding?.let { cookie.encoding = it }
        config.domain?.let { cookie.domain = it }
        config.path?.let { cookie.path = it }
        config.secure?.let { cookie.secure = it }
        config.httpOnly?.let { cookie.httpOnly = it }
        config.extensions?.let { cookie.extensions + it }
        config.encryption?.let { encryption ->
            transform(
                encryption.encryptionKey?.let {
                    SessionTransportTransformerEncrypt(
                        encryptionKey = it.toByteArray(),
                        signKey = encryption.signKey.toByteArray(),
                        encryptAlgorithm = encryption.encryptAlgorithm,
                        signAlgorithm = encryption.signAlgorithm,
                    )
                } ?: SessionTransportTransformerMessageAuthentication(
                    encryption.signKey.toByteArray(),
                    encryption.signAlgorithm,
                ),
            )
        }
    }
    (config.filePath?.let {
        cookie<S>(name, directorySessionStorage(File(it)), cookieBuilder)
    } ?: config.inMemory?.let {
        cookie<S>(
            name,
            SessionStorageMemory(),
            cookieBuilder,
        )
    } ?: cookie<S>(
        name,
        cookieBuilder,
    ))
}
