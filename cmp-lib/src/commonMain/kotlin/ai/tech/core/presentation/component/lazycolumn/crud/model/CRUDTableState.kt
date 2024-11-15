package ai.tech.core.presentation.component.lazycolumn.crud.model

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
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

public class CRUDTableState<ID : Any>(
    public val searchFieldStates: List<SearchFieldState>,
    sort: List<Order> = emptyList(),
    limitOffset: LimitOffset = LimitOffset(0, 10),
    liveSearch: Boolean = true,
    showActions: Boolean = true,
    showPagination: Boolean = true,
    showSelect: Boolean = true,
    showHeader: Boolean = true,
    showSearch: Boolean = true,
) {

    public constructor(
        searchFieldStates: List<SearchFieldState>,
        data: CRUDTableStateData,
    ) : this(
        searchFieldStates,
        data.sort,
        LimitOffset(0, data.limit),
        data.liveSearch,
        data.showPagination,
        data.showActions,
        data.showSelect,
        data.showHeader,
        data.showSearch,
    )

    public var sort: SnapshotStateList<Order> = sort.toMutableStateList()

    public var liveSearch: Boolean by mutableStateOf(liveSearch)

    public var showActions: Boolean by mutableStateOf(showActions)

    public var showPagination: Boolean by mutableStateOf(showPagination)

    public var showSelect: Boolean by mutableStateOf(showSelect)

    public var showHeader: Boolean by mutableStateOf(showHeader)

    public var showSearch: Boolean by mutableStateOf(showSearch)

    public var limitOffset: LimitOffset by mutableStateOf(limitOffset)

    public fun predicate(): BooleanVariable? = null

    public companion object {

        @Suppress("UNCHECKED_CAST")
        public fun <ID : Any> Saver(): Saver<CRUDTableState<ID>, *> = listSaver(
            save = {
                listOf(
                    it.searchFieldStates,
                    it.sort,
                    it.liveSearch,
                    it.showActions,
                    it.showPagination,
                    it.showSelect,
                    it.showHeader,
                    it.showSearch,
                    it.limitOffset,
                )
            },
            restore = {
                CRUDTableState(
                    it[0] as List<SearchFieldState>,
                    it[1] as List<Order>,
                    it[2] as LimitOffset,
                    it[3] as Boolean,
                    it[4] as Boolean,
                    it[5] as Boolean,
                    it[6] as Boolean,
                    it[7] as Boolean,
                    it[8] as Boolean,
                )
            },
        )
    }
}

@Composable
public fun <ID : Any> rememberCRUDTableState(state: CRUDTableState<ID>): CRUDTableState<ID> =
    rememberSaveable(saver = CRUDTableState.Saver()) { state }

@Composable
public fun <ID : Any> rememberCRUDTableState(
    properties: List<String>,
    data: CRUDTableStateData,
): CRUDTableState<ID> =
    rememberCRUDTableState(
        CRUDTableState(
            properties.map { property ->
                data.searchFieldStates.entries.find { it.key == property }
                    ?.let { rememberSearchFieldState(it.value) } ?: rememberSearchFieldState()
            },
            data,
        ),
    )
