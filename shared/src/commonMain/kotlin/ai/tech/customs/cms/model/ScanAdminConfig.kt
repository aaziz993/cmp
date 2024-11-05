package customs.cms.model

import core.presentation.component.datatable.model.config.CRUDTableConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ScanAdminConfig(
    val crudTable: CRUDTableConfig = CRUDTableConfig()
)
