package ai.tech.core.misc.location.model.config

import ai.tech.core.data.crud.model.config.CRUDRepositoryConfigImpl
import kotlinx.serialization.Serializable

@Serializable
public data class ServerLocationConfig(
    override val repository: CRUDRepositoryConfigImpl = CRUDRepositoryConfigImpl(),
): SharedLocationConfig
