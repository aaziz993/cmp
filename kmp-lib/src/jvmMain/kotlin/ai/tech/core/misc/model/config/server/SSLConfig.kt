package ai.tech.core.misc.model.config.server

import ai.tech.core.misc.model.config.EnabledConfig
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.network.tls.extensions.HashAlgorithm
import io.ktor.network.tls.extensions.SignatureAlgorithm
import java.io.File
import java.io.FileInputStream
import java.net.Inet4Address
import java.security.KeyStore
import javax.security.auth.x500.X500Principal
import kotlinx.serialization.Serializable

@Serializable
public data class SSLConfig(
    val keyStorePath: String = "server/src/main/resources/cert/keystore.p12",
    val keyStorePassword: String,
    val keyAlias: String = "serverSSL",
    val hash: String? = null,
    val sign: String? = null,
    val privateKeyPassword: String,
    val subject: String? = null,
    val daysValid: Long? = null,
    val keySizeInBits: Int? = null,
    val domains: List<String>? = null,
    val ipAddresses:List<String>?=null,
    val format: String = "PKCS12",
    val generate: Boolean = false,
    val rewrite: Boolean = false,
    val port: Int = 443,
    override val enable: Boolean = true
) : EnabledConfig{

    public val keyStore: KeyStore
        get() {
            val keyStoreFile = File(keyStorePath)

            if (!generate || (keyStoreFile.exists() && !rewrite)) {
                return KeyStore.getInstance(format).apply {
                    load(FileInputStream(keyStoreFile), keyStorePassword.toCharArray())
                }
            }

            val thisRef=this

            return buildKeyStore {
                certificate(keyAlias) {
                    thisRef.hash?.let { hash = HashAlgorithm.valueOf(it) }
                    thisRef.sign?.let { sign = SignatureAlgorithm.valueOf(it) }
                    password = privateKeyPassword
                    thisRef.subject?.let { subject = X500Principal(it) }
                    thisRef.daysValid?.let { daysValid = it }
                    thisRef.keySizeInBits?.let { keySizeInBits = it }
                    thisRef.domains?.let { domains = it }
                    thisRef.ipAddresses?.let { ipAddresses = it.map(Inet4Address::getByName) }
                }
            }.also { it.saveToFile(keyStoreFile, keyStorePassword) }
        }
}
