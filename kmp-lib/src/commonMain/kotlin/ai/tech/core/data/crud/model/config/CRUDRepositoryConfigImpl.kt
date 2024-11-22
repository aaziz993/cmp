package ai.tech.core.data.crud.model.config

import ai.tech.core.misc.auth.model.AuthResource
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDRepositoryConfigImpl(
    override val timeZone: String? = null,
    override val readAuth: AuthResource? = null,
    override val writeAuth: AuthResource? = readAuth,
) : CRUDRepositoryConfig
