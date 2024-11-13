package ai.tech.core.misc.consul.client.acl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Role(
    @SerialName("Name")
val name: String,
    @SerialName("ID")
val id: String? = null,
    @SerialName("Description")
val description: String? = null,
    @SerialName("Policies")
val policies: List<RolePolicyLink>,
    @SerialName("ServiceIdentities")
val serviceIdentities: List<RoleServiceIdentity>,
    @SerialName("NodeIdentities")
val nodeIdentities: List<RoleNodeIdentity>,
    @SerialName("Namespace")
val namespace: String? = null,
) {

    @Serializable
    public data class RolePolicyLink(
        @SerialName("ID")
val id: String? = null,
        @SerialName("Name")
val name: String? = null,
    )

    @Serializable
    public data class RoleServiceIdentity(
        @SerialName("ServiceName")
val name: String,
        @SerialName("Datacenters")
val datacenters: List<String>
    )

    @Serializable
    public data class RoleNodeIdentity(
        @SerialName("NodeName")
val name: String,
        @SerialName("Datacenter")
val datacenter: String
    )
}
