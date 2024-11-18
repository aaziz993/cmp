package ai.tech.core.presentation.component.lazycolumn.crud

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowheadLeft
import compose.icons.evaicons.outline.ArrowheadRight
import compose.icons.evaicons.outline.ChevronLeft
import compose.icons.evaicons.outline.ChevronRight
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Composable
private fun <T : Any> PaginationRow(
    contentPadding: PaddingValues,
    state: CRUDTableState<T>,
    pageCount: Int,
    onClick: () -> Unit,
) {
    Row(
        Modifier.padding(contentPadding).wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pageDouble = state.limitOffset.offset!! / state.limitOffset.limit!!.toDouble()
        val pageInt = pageDouble.toInt()
        val nextPageCount = ceil(
            max(
                state.totalItemsCount - (pageInt + 1) * state.limitOffset.limit!!, 0
            ) / state.limitOffset.limit!!.toDouble()
        ).toLong()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${state.limitOffset.offset}..${state.limitOffset.offset!! + state.rowItems.count { !it.isNew }}-${state.totalItemsCount}",
                style = MaterialTheme.typography.labelMedium
            )
        }
        IconButton(
            onClick = {
                state.setOffset()
                onClick()
            },
            enabled = pageInt > 0,
        ) {
            Icon(EvaIcons.Outline.ArrowheadLeft, "First")
        }
        IconButton(
            {
                state.setOffset((pageInt - 1) * state.limitOffset.limit!!)
                onClick()
            },
            enabled = pageInt > 0,
        ) {
            Icon(EvaIcons.Outline.ChevronLeft, "Previous")
        }
        (0 until min(pageCount.toLong(), nextPageCount + 1)).map {
            val pageOffset = pageInt + it
            OutlinedIconButton({
                state.setOffset(pageOffset * state.limitOffset.limit!!)
                onClick()
            }) {
                Text(pageOffset.toString())
            }
        }
        IconButton(
            {
                state.setOffset((pageInt + 1) * state.limitOffset.limit!!)
                onClick()
            }, enabled = nextPageCount > 0
        ) {
            Icon(EvaIcons.Outline.ChevronRight, "Next")
        }
        IconButton(
            {
                state.setMaxOffset()
                onClick()
            }, enabled = nextPageCount > 0
        ) {
            Icon(EvaIcons.Outline.ArrowheadRight, "Last")
        }
    }
}
