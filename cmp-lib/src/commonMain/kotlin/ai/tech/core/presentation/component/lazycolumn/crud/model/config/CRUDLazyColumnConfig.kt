package ai.tech.core.presentation.component.lazycolumn.crud.model.config

import ai.tech.core.data.crud.model.config.CRUDRepositoryConfig
import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnStateData
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDLazyColumnConfig(
    override val timeZone: String? = null,
    override val readAuth: AuthResource? = null,
    override val writeAuth: AuthResource? = readAuth,
    val isReadOnly: Boolean = false,
    val isMultiSort: Boolean = true,
    val showPageCount: Int = 10,
    val state: CRUDLazyColumnStateData = CRUDLazyColumnStateData()
) : CRUDRepositoryConfig
