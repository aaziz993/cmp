package ai.tech.core.presentation.component.lazycolumn.crud.model.config

import ai.tech.core.data.crud.model.config.CRUDRepositoryConfig
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnStateData
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDLazyColumnConfig(
    val repository: CRUDRepositoryConfig = CRUDRepositoryConfig(),
    val isReadOnly: Boolean = false,
    val isMultiSort: Boolean = true,
    val showPageCount: Int = 10,
    val state: CRUDLazyColumnStateData = CRUDLazyColumnStateData()
)
