package ai.tech.home.map.location.model

import kotlinx.serialization.Serializable

@Serializable
public data class LocationConfig(
    val crudTable: CRUDTableConfig = CRUDTableConfig()
)
