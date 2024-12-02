package ai.tech.core.misc.auth.keycloak.client.admin.model

import ai.tech.core.misc.type.serialization.decodeAnyFromJsonElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
public data class RoleRepresentation(
    @SerialName("attributes")
    private val _attributes: JsonElement? = null,
    val clientRole: Boolean? = null,
    val composite: Boolean? = null,
    val containerId: String? = null,
    val description: String? = null,
    val id: String? = null,
    val name: String? = null,
) {

    @Transient
    public val attributes: Map<String, Any>? =
        _attributes?.let(Json.Default::decodeAnyFromJsonElement) as Map<String, Any>?
}
