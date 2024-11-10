package map.location.model

import core.presentation.component.datatable.model.config.CRUDTableConfig
import kotlinx.serialization.Serializable

@Serializable
public data class GeolocationConfig(
    val crudTable: CRUDTableConfig = CRUDTableConfig()
)
