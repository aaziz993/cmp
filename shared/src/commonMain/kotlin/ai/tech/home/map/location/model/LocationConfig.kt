package map.location.model

import core.presentation.component.datatable.model.config.CRUDTableConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LocationConfig(
    val crudTable: CRUDTableConfig = CRUDTableConfig()
)
