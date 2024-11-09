package ai.tech.core.misc.plugin.session

import ai.tech.core.misc.plugin.session.model.config.CookieConfig
import io.ktor.server.sessions.CookieSessionBuilder
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.SessionsConfig
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.directorySessionStorage
import java.io.File

public inline fun <reified S : Any> SessionsConfig.cookie(
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
