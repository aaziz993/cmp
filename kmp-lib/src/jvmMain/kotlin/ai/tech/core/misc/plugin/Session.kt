package ai.tech.core.misc.plugin

import core.auth.model.ServerAuthConfig
import core.io.model.http.server.session.CookieConfig
import core.io.model.http.server.session.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import java.io.File

public fun Application.configSession(config: ServerAuthConfig?) {
    config?.let {
        install(Sessions) {
            it.jwtHs256.forEach { (name, config) ->
                cookie<UserSession>(name, config.cookie)
            }

            it.jwtRs256.forEach { (name, config) ->
                cookie<UserSession>(name, config.cookie)
            }

            it.oauths.forEach { (name, config) ->
                cookie<UserSession>(name, config.cookie)
            }
        }
    }
}

private inline fun <reified S : Any> SessionsConfig.cookie(
    name: String,
    config: CookieConfig?,
) = if (config == null) {
    cookie<S>(name)
} else {
    val cookieBuilder: CookieSessionBuilder<S>.() -> Unit = {
        config.maxAgeInSeconds?.let { cookie.maxAgeInSeconds = it }
        config.encoding?.let { cookie.encoding = it }
        config.domain?.let { cookie.domain = it }
        config.path?.let { cookie.path = it }
        config.secure?.let { cookie.secure = it }
        config.httpOnly?.let { cookie.httpOnly = it }
        config.extensions?.let { cookie.extensions + it }
        config.encryption?.let { encryption ->
            transform(encryption.encryptionKey?.let {
                SessionTransportTransformerEncrypt(
                    encryptionKey = it.toByteArray(),
                    signKey = encryption.signKey.toByteArray(),
                    encryptAlgorithm = encryption.encryptAlgorithm,
                    signAlgorithm = encryption.signAlgorithm,
                )
            } ?: SessionTransportTransformerMessageAuthentication(
                encryption.signKey.toByteArray(),
                encryption.signAlgorithm
            ))
        }
    }
    (config.filePath?.let {
        cookie<S>(name, directorySessionStorage(File(it)), cookieBuilder)
    } ?: config.inMemory?.let {
        cookie<S>(
            name,
            SessionStorageMemory(),
            cookieBuilder
        )
    } ?: cookie<S>(
        name,
        cookieBuilder
    ))
}
