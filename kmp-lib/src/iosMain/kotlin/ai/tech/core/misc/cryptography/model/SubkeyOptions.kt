package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SubkeyOptions(
    var type: String? = null,
    var curve: String? = null,
    var rsaBits: Double? = null,
//    var date: Date?,
    var sign: Boolean? = null,
    var config: Config? = null,
)
