package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.consul.client.serializer.ConsulDurationSerializer
import kotlin.time.Duration
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Token(
    @SerialName("AccessorID")
    val id: String? = null,
    @SerialName("SecretID")
    val secretId: String? = null,
    @SerialName("Description")
    val description: String? = null,
    @SerialName("Policies")
    val policies: List<PolicyLink>,
    @SerialName("Roles")
    val roles: List<RoleLink>,
    @SerialName("ServiceIdentities")
    val serviceIdentities: List<ServiceIdentity>,
    @SerialName("NodeIdentities")
    val nodeIdentities: List<NodeIdentity>,
    @SerialName("Local")
    val local: Boolean? = null,
    @SerialName("ExpirationTime")
    val expirationTime: Instant? = null,
    @Serializable(with = ConsulDurationSerializer::class)
    @SerialName("ExpirationTTL")
    val expirationTTL: Duration? = null,
    @SerialName("Namespace")
    val namespace: String?
) {

    @Serializable
    public data class PolicyLink(
        @SerialName("ID")
        val id: String? = null,
        @SerialName("Name")
        val name: String?
    )

    @Serializable
    public data class RoleLink(
        @SerialName("ID")
        val id: String? = null,
        @SerialName("Name")
        val name: String?
    )

    @Serializable
    public data class ServiceIdentity(
        @SerialName("ServiceName")
        val name: String,
        @SerialName("Datacenters")
        val datacenters: List<String>
    )

    @Serializable
    public data class NodeIdentity(
        @SerialName("NodeName")
        val name: String,
        @SerialName("Datacenter")
        val datacenter: String
    )
}
