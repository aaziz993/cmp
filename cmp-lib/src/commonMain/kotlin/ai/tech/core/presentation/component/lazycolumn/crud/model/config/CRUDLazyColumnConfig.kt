package ai.tech.core.presentation.component.lazycolumn.crud.model.config

import ai.tech.core.data.crud.model.config.CRUDRepositoryConfig
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnStateData
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDLazyColumnConfig(
    val repository: CRUDRepositoryConfig = CRUDRepositoryConfig(),
    val readOnly: Boolean = false,
    val paginationPageCount: Int = 10,
    val multiSort: Boolean = true,
    val state: CRUDLazyColumnStateData = CRUDLazyColumnStateData()
)