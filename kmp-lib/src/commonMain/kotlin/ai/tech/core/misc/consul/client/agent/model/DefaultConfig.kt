package ai.tech.core.misc.consul.client.agent.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DebugConfig(
    @SerialName("Bootstrap")
val bootstrap: Boolean = false,
    @SerialName("SkipLeaveOnInt")
val skipLeaveOnInt: Boolean = false,
    @SerialName("Datacenter")
val datacenter: String? = null,
    @SerialName("DataDir")
val dataDir: String? = null,
    @Suppress("SpellCheckingInspection")
    @SerialName("DNSRecursors")
val recursors: List<String> = listOf(),
    @SerialName("DNSDomain")
val dnsDomain: String? = null,
    @SerialName("LogLevel")
val logLevel: String? = null,
    @SerialName("NodeName")
val nodeName: String? = null,
    @Suppress("SpellCheckingInspection")
    @SerialName("ClientAddrs")
val clientAddrs: List<String> = listOf(),
    @SerialName("BindAddr")
val bindAddr: String? = null,
    @SerialName("LeaveOnTerm")
val leaveOnTerm: Boolean = false,
    @SerialName("EnableDebug")
val enableDebug: Boolean = false,
    @SerialName("VerifyIncoming")
val verifyIncoming: Boolean = false,
    @SerialName("VerifyOutgoing")
val verifyOutgoing: Boolean = false,
    @SerialName("CAFile")
val caFile: String? = null,
    @SerialName("CertFile")
val certFile: String? = null,
    @SerialName("KeyFile")
val keyFile: String? = null,
    @SerialName("UiDir")
val uiDir: List<String> = listOf(),
    @SerialName("PidFile")
val pidFile: String? = null,
    @SerialName("EnableSyslog")
val enableSyslog: Boolean = false,
    @SerialName("RejoinAfterLeave")
val rejoinAfterLeave: Boolean = false,
    @SerialName("AdvertiseAddrLAN")
val advertiseAddrLAN: String? = null,
    @SerialName("AdvertiseAddrWAN")
val advertiseAddrWAN: String? = null
)
