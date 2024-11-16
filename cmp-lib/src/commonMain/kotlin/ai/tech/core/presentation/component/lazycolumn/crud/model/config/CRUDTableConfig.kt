package ai.tech.core.presentation.component.lazycolumn.crud.model.config

import ai.tech.core.data.crud.model.config.CRUDRepositoryConfig
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableStateData
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDTableConfig(
    val repository: CRUDRepositoryConfig = CRUDRepositoryConfig(),
    val readOnly: Boolean = false,
    val paginationPageCount: Int = 10,
    val multiSort: Boolean = true,
    val state: CRUDTableStateData = CRUDTableStateData()
)
