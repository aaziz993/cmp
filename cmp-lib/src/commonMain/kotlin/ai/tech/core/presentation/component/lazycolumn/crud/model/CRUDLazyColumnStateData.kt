package ai.tech.core.presentation.component.lazycolumn.crud.model

import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldStateData
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.serialization.Serializable

@Serializable
public data class CRUDLazyColumnStateData(
    val searchFieldStates: Map<String, SearchFieldStateData> = emptyMap(),
    val sort: List<Order> = emptyList(),
    val isMultiSort: Boolean = true,
    val isLiveSearch: Boolean = true,
    val liveSearchDebounce: Duration = 1.toDuration(DurationUnit.SECONDS),
    val isPrepend: Boolean = true,
    val showPagination: Boolean = true,
    val showActions: Boolean = true,
    val showSelect: Boolean = true,
    val showHeader: Boolean = true,
    val showSearch: Boolean = true,
    val initialLoad: Long = 10,
    val limit: Long = 10,
)
