package ai.tech.core.data.database.model.config

import ai.tech.core.misc.auth.model.AuthResource
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDRepositoryConfig(
    val timeZone: String? = null,
    val readAuth: AuthResource? = null,
    val saveAuth: AuthResource? = readAuth,
    val updateAuth: AuthResource? = readAuth,
    val deleteAuth: AuthResource? = readAuth,
)
