package ai.tech.core.misc.cryptography.model;

import kotlinx.serialization.Serializable

@Serializable
internal data class SubkeyOptions (
    var type: String?,
    var curve: String?,
    var rsaBits: Double?,
//    var date: Date?,
    var sign: Boolean?,
    var config: Config?,
)
