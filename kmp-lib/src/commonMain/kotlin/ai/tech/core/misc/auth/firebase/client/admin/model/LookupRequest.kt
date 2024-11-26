package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class LookupRequest(
    val idToken: String? = null,
    val localId: List<String>? = null,
    val email: List<String>? = null,
    val phoneNumber: List<String>? = null,
    val federatedUserId: List<FederatedUserId>? = null
)
