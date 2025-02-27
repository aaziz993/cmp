package ai.tech.core.presentation.component.lazycolumn.crud.model

import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldStateData
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDLazyColumnStateData(
    val searchFieldStates: Map<String, SearchFieldStateData> = emptyMap(),
    val sort: List<Order> = emptyList(),
    val isMultiSort: Boolean = true,
    val isLiveSearch: Boolean = true,
    val liveSearchDebounce: Duration = 1.seconds,
    val isPrepend: Boolean = true,
    val showPagination: Boolean = true,
    val showActions: Boolean = true,
    val showSelect: Boolean = true,
    val showHeader: Boolean = true,
    val showSearch: Boolean = true,
    val initialLoad: Long = 10,
    val limit: Long = 10,
)
