package ai.tech.core.misc.model.config.server.keystore

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.server.ServerSSLConfig
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import java.io.File
import kotlinx.serialization.Serializable

@Serializable
public data class GenerateServerSSLConfig(
    override val keyStore: String = "server/src/main/resources/cert/keystore.p12",
    override val keyStorePassword: String,
    override val keyAlias: String = "serverSSL",
    override val privateKeyPassword: String,
    val daysValid: Long? = null,
    val keySizeInBits: Int? = null,
    val domains: List<String>? = null,
    val rewrite: Boolean? = null,
    override val enable: Boolean? = null
) : ServerSSLConfig, EnabledConfig {

    public fun generate() {
        if (enable == false) {
            return
        }

        val keyStoreFile = File(keyStore)

        if (keyStoreFile.exists() && rewrite == true) {
            return
        }

        val thisRef = this

        val keyStore = buildKeyStore {
            certificate(keyAlias) {
                password = privateKeyPassword
                thisRef.daysValid?.let { daysValid = it }
                thisRef.keySizeInBits?.let { keySizeInBits = it }
                domains = thisRef.domains ?: listOf("127.0.0.1", "0.0.0.0", "localhost")
            }
        }

        keyStore.saveToFile(keyStoreFile, keyStorePassword)
    }
}
