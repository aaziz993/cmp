package ai.tech.core.misc.consul.client.agent.model

import kotlin.time.Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Check(
    @SerialName("Name")
    val name: String,
    @SerialName("Args")
    val args: List<String>,
    @SerialName("Id")
    val id: String? = null,
    @SerialName("Interval")
    val interval: String? = null,
    @SerialName("Notes")
    val notes: String? = null,
    @SerialName("DeregisterCriticalServiceAfter")
    val deregisterCriticalServiceAfter: String? = null,
    @SerialName("AliasNode")
    val aliasNode: String? = null,
    @SerialName("AliasService")
    val aliasService: String? = null,
    @SerialName("DockerContainerId")
    val dockerContainerId: String? = null,
    @SerialName("GRPC")
    val grpc: String? = null,
    @SerialName("GRPCUseTLS")
    val grpcUseTLS: Boolean? = null,
    @SerialName("HTTP")
    val http: String? = null,
    @SerialName("Method")
    val method: String? = null,
    @SerialName("Body")
    val body: String? = null,
    @SerialName("Header")
    val header: Map<String, List<String>>? = null,
    @SerialName("Timeout")
    val timeout: Duration? = null,
    @SerialName("OutputMaxSize")
    val outputMaxSize: Int? = null,
    @SerialName("TLSSkipVerify")
    val tlsSkipVerify: Boolean? = null,
    @SerialName("TCP")
    val tcp: String? = null,
    @SerialName("TTL")
    val ttl: String? = null,
    @SerialName("ServiceId")
    val serviceId: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("SuccessBeforePassing")
    val successBeforePassing: Int? = null,
    @SerialName("FailureBeforeCritical")
    val failureBeforeCritical: Int? = null,
)
