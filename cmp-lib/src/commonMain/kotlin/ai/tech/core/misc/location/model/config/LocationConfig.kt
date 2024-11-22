package ai.tech.core.misc.location.model.config

import ai.tech.core.presentation.component.lazycolumn.crud.model.config.CRUDLazyColumnConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LocationConfig(
    override val repository: CRUDLazyColumnConfig = CRUDLazyColumnConfig()
) : SharedLocationConfig
