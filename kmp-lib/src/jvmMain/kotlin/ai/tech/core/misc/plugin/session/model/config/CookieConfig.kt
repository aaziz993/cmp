package ai.tech.core.misc.plugin.session.model.config

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
public data class CookieConfig(
    val inMemory: Boolean? = null,
    val filePath: String? = null,
    val maxAgeInSeconds: Long? = null,
    val encoding: CookieEncoding? = null,
    val domain: String? = null,
    val path: String? = null,
    val secure: Boolean? = null,
    val httpOnly: Boolean? = null,
    val extensions: MutableMap<String, String?>? = null,
    val encryption: SessionEncryptConfig? = null,
)
