package ai.tech.core.misc.consul.client.agent.model

import ai.tech.core.misc.consul.client.catalog.model.ServiceWeights
import ai.tech.core.misc.consul.client.health.model.ServiceHealth
import kotlin.time.Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Registration(
    @SerialName("Name")
    val name: String,
    @SerialName("Id")
    val id: String? = null,
    @SerialName("Tags")
    val tags: List<String>? = null,
    @SerialName("Address")
    val address: String? = null,
    @SerialName("TaggedAddress")
    val taggedAddress: Map<String, *>? = null,
    @SerialName("Meta")
    val meta: Map<String, String>? = null,
    @SerialName("Port")
    val port: Int? = null,
    @SerialName("Kind")
    val kind: String? = null,
    @SerialName("Proxy")
    val proxy: ServiceProxy? = null,
    @SerialName("Connect")
    val connect: Connect? = null,
    @SerialName("Check")
    val check: ServiceHealth? = null,
    @SerialName("Checks")
    val checks: List<RegCheck>,
    @SerialName("EnableTagOverride")
    val enableTagOverride: Boolean? = null,
    @SerialName("Weights")
    val serviceWeights: ServiceWeights? = null,
) {

    @Serializable
    public data class RegCheck(
        @SerialName("CheckID")
        val id: String? = null,
        @SerialName("Name")
        val name: String? = null,
        @SerialName("Args")
        val args: List<String>? = null,
        @SerialName("Interval")
        val interval: Duration? = null,
        @SerialName("TTL")
        val ttl: Duration? = null,
        @SerialName("HTTP")
        val http: String? = null,
        @SerialName("TCP")
        val tcp: String? = null,
        @SerialName("GRPC")
        val grpc: String? = null,
        @SerialName("GRPCUseTLS")
        val grpcUseTls: Boolean? = null,
        @SerialName("Timeout")
        val timeout: String? = null,
        @SerialName("Notes")
        val notes: String? = null,
        @SerialName("DeregisterCriticalServiceAfter")
        val deregisterCriticalServiceAfter: String? = null,
        @SerialName("TLSSkipVerify")
        val tlsSkipVerify: Boolean? = null,
        @SerialName("Status")
        val status: String? = null,
        @SerialName("SuccessBeforePassing")
        val successBeforePassing: Int? = null,
        @SerialName("FailuresBeforeCritical")
        val failuresBeforeCritical: Int? = null,
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
}
