package ai.tech.core.data.database.model.config

import ai.tech.core.auth.model.AuthResource
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDRepositoryConfig(
    val timeZone: String? = null,
    val auth: AuthResource? = null,
    val saveAuth: AuthResource? = auth,
    val updateAuth: AuthResource? = auth,
    val deleteAuth: AuthResource? = auth,
)
