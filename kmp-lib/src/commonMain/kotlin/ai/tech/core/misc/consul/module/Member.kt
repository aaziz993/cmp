package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Member(
    @SerialName("Addr") val address: String? = null,
    @SerialName("Name") val name: String? = null,
    @SerialName("Port") val port: Int? = null,
    @SerialName("Tags") val tags: Map<String, String> = mapOf(),
    @SerialName("Status") val status: Int? = null,
    @SerialName("ProtocolMin") val protocolMin: Int? = null,
    @SerialName("ProtocolMax") val protocolMax: Int? = null,
    @SerialName("ProtocolCur") val protocolCur: Int? = null,
    @SerialName("DelegateMin") val delegateMin: Int? = null,
    @SerialName("DelegateMax") val delegateMax: Int? = null,
    @SerialName("DelegateCur") val delegateCur: Int? = null
)
