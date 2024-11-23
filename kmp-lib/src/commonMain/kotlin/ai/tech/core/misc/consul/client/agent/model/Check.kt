package ai.tech.core.misc.consul.client.agent.model

import ai.tech.core.misc.consul.client.serializer.ConsulDurationSerializer
import kotlin.time.Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Check(
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Args")
    val args: List<String>? = null,
    @SerialName("ID")
    val id: String? = null,
    @Serializable(with = ConsulDurationSerializer::class)
    @SerialName("Interval")
    val interval: Duration? = null,
    @SerialName("Notes")
    val notes: String? = null,
    @Serializable(with = ConsulDurationSerializer::class)
    @SerialName("DeregisterCriticalServiceAfter")
    val deregisterCriticalServiceAfter: Duration? = null,
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
    @Serializable(with = ConsulDurationSerializer::class)
    @SerialName("Timeout")
    val timeout: Duration? = null,
    @SerialName("OutputMaxSize")
    val outputMaxSize: Int? = null,
    @SerialName("Output")
    val output: String? = null,
    @SerialName("TLSSkipVerify")
    val tlsSkipVerify: Boolean? = null,
    @SerialName("TCP")
    val tcp: String? = null,
    @Serializable(with = ConsulDurationSerializer::class)
    @SerialName("TTL")
    val ttl: Duration? = null,
    @SerialName("ServiceID")
    val serviceId: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("SuccessBeforePassing")
    val successBeforePassing: Int? = null,
    @SerialName("FailureBeforeCritical")
    val failureBeforeCritical: Int? = null,
    @SerialName("ServiceTags")
    val serviceTags: List<String>? = null
) {

    init {
        require(
            !(http == null && ttl == null &&
                args == null && tcp == null && grpc == null),
        ) {
            "Check must specify either http, tcp, ttl, grpc or args"
        }

        if (!(http == null && args == null && tcp == null && grpc == null)) {
            require(interval != null) {
                "Interval must be set if check type is http, tcp, grpc or args"
            }
        }
    }
}
