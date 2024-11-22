package ai.tech.core.misc.model.config.server

import ai.tech.core.misc.cryptography.model.HashAlgorithm
import ai.tech.core.misc.model.config.EnabledConfig
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
) : EnabledConfig

