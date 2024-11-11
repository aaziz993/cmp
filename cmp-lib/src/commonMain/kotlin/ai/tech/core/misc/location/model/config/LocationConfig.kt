package ai.tech.core.misc.location.model.config

import ai.tech.core.presentation.component.lazycolumn.crud.model.config.CRUDTableConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LocationConfig(
    val crudTable: CRUDTableConfig = CRUDTableConfig()
)
