package ai.tech.core.presentation.component.lazycolumn.crud.model

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.misc.type.multiple.removeFirst
import ai.tech.core.misc.type.multiple.replaceFirst
import ai.tech.core.misc.type.multiple.replaceWith
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import ai.tech.core.presentation.component.textfield.search.model.rememberSearchFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

public class CRUDLazyColumnState(
    public val searchFieldStates: List<SearchFieldState>,
    sort: List<Order> = emptyList(),
    limitOffset: LimitOffset = LimitOffset(0, 10),
    isMultiSort: Boolean = true,
    isLiveSearch: Boolean = true,
    liveSearchDebounce: String = "1s",
    isPrepend: Boolean = true,
    showActions: Boolean = true,
    showPagination: Boolean = true,
    showSelect: Boolean = true,
    showHeader: Boolean = true,
    showSearch: Boolean = true,
) {

    public constructor(
        searchFieldStates: List<SearchFieldState>,
        data: CRUDLazyColumnStateData,
    ) : this(
        searchFieldStates,
        data.sort,
        LimitOffset(0, data.limit),
        data.isMultiSort,
        data.isLiveSearch,
        data.liveSearchDebounce.toString(),
        data.isPrepend,
        data.showPagination,
        data.showActions,
        data.showSelect,
        data.showHeader,
        data.showSearch,
    )

    public var sort: SnapshotStateList<Order> = sort.toMutableStateList()

    public var isMultiSort: Boolean by mutableStateOf(isMultiSort)

    public var isLiveSearch: Boolean by mutableStateOf(isLiveSearch)

    public var liveSearchDebounce: String by mutableStateOf(liveSearchDebounce)

    public var isPrepend: Boolean by mutableStateOf(isPrepend)

    public var showActions: Boolean by mutableStateOf(showActions)

    public var showPagination: Boolean by mutableStateOf(showPagination)

    public var showSelect: Boolean by mutableStateOf(showSelect)

    public var showHeader: Boolean by mutableStateOf(showHeader)

    public var showSearch: Boolean by mutableStateOf(showSearch)

    public var limitOffset: LimitOffset by mutableStateOf(limitOffset)

    public fun getIndexedOrder(property: EntityProperty): IndexedValue<Order>? =
        sort.withIndex().find { (_, order) -> order.name == property.name }

    public fun order(property: EntityProperty) {
        val order = getIndexedOrder(property)?.value

        when {
            order == null -> Order(property.name).let {
                if (isMultiSort) {
                    sort += it
                }
                else {
                    sort.replaceWith(listOf(it))
                }
            }

            order.ascending -> sort.replaceFirst({ it.name == property.name }) { Order(name, false) }

            else -> sort.removeFirst { it.name == property.name }
        }
    }

    public companion object {

        @Suppress("UNCHECKED_CAST")
        public fun Saver(): Saver<CRUDLazyColumnState, *> = listSaver(
            save = {
                listOf(
                    it.searchFieldStates,
                    it.sort,
                    it.isMultiSort,
                    it.liveSearchDebounce,
                    it.isPrepend,
                    it.showActions,
                    it.showPagination,
                    it.showSelect,
                    it.showHeader,
                    it.showSearch,
                    it.limitOffset,
                )
            },
            restore = {
                CRUDLazyColumnState(
                    it[0] as List<SearchFieldState>,
                    it[1] as List<Order>,
                    it[2] as LimitOffset,
                    it[3] as Boolean,
                    it[4] as Boolean,
                    it[5] as String,
                    it[6] as Boolean,
                    it[7] as Boolean,
                    it[8] as Boolean,
                    it[9] as Boolean,
                    it[10] as Boolean,
                    it[11] as Boolean,
                )
            },
        )
    }
}

@Composable
public fun rememberCRUDTableState(state: CRUDLazyColumnState): CRUDLazyColumnState =
    rememberSaveable(saver = CRUDLazyColumnState.Saver()) { state }

@Composable
public fun rememberCRUDTableState(
    properties: List<String>,
    data: CRUDLazyColumnStateData,
): CRUDLazyColumnState =
    rememberCRUDTableState(
        CRUDLazyColumnState(
            properties.map { property ->
                data.searchFieldStates.entries.find { it.key == property }
                    ?.let { rememberSearchFieldState(it.value) } ?: rememberSearchFieldState()
            },
            data,
        ),
    )
