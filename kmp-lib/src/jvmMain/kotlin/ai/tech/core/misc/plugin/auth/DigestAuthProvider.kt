package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.basic.model.config.DigestConfig
import ai.tech.core.misc.plugin.auth.model.config.DigestAuthProviderConfig
import io.ktor.util.getDigestFunction

public interface DigestAuthProvider {

    public val config: DigestAuthProviderConfig

    public val digester: (String) -> ByteArray
        get() =
            config.digest?.takeIf(EnabledConfig::enable)?.let(DigestConfig::algorithm)?.let(::getDigester)
                ?: String::toByteArray

    public companion object {

        public fun getDigester(algorithm: String): (String) -> ByteArray = getDigestFunction(algorithm) { it }
    }
}
