package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GenerateKeyOptions(
    var userIDs: List<PGPUserId>?,
    var passphrase: String?,
    var type: String,
    var curve: String?,
    var rsaBits: Double?,
    var keyExpirationTime: Double?,
//    var date: Date?,
    var subkeys: List<SubkeyOptions>?,
    var format: String,
    var config: Config?,
)
