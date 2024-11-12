package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.model.config.DigestAuthProviderConfig
import ai.tech.core.misc.plugin.auth.model.config.DigestConfig
import io.ktor.util.getDigestFunction

public interface DigestAuthProvider {

    public val config: DigestAuthProviderConfig

    public fun getDigester(): (String) -> ByteArray =
        config.digest?.takeIf(EnabledConfig::enable)?.let(DigestConfig::algorithm)?.let(::getDigester)
            ?: String::toByteArray

    public companion object {

        public fun getDigester(algorithm: String): (String) -> ByteArray = getDigestFunction(algorithm) { it }
    }
}
