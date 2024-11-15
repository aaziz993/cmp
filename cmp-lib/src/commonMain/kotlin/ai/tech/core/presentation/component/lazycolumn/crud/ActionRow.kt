package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.presentation.component.column.expandable.ExpandableSection
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.uuid4
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Close
import compose.icons.evaicons.outline.Copy
import compose.icons.evaicons.outline.Edit
import compose.icons.evaicons.outline.Minus
import compose.icons.evaicons.outline.Plus
import compose.icons.evaicons.outline.Save
import compose.icons.evaicons.outline.Trash2
import compose.icons.evaicons.outline.Upload
import core.presentation.component.datatable.model.CRUDTableLocalization
import core.presentation.component.datatable.model.RowItem

@Composable
internal fun <ID : Any> ActionRow(
    state: CRUDTableState<ID>,
    contentPadding: PaddingValues,
    readOnly: Boolean,
    add: Boolean,
    update: Boolean,
    delete: Boolean,
    downloadAllIcon: @Composable () -> Unit,
    localization: CRUDTableLocalization,
    onDownload: (() -> Unit)? = null,
    onUpload: (() -> Unit)? = null,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        Modifier.padding(contentPadding).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        val selectExists = state.selectExists

        val selectAdds = state.selectAdds

        onDownload?.takeIf { selectExists.isNotEmpty() }?.let {
            IconButton(it) {
                downloadAllIcon()
            }
        }

        if (!readOnly) {
            onUpload?.let {
                IconButton(it) {
                    Icon(EvaIcons.Outline.Upload, null)
                }
            }

            if (selectExists.isNotEmpty()) {
                IconButton({
                    state.rowItems.addAll(
                        lazyListState.firstVisibleItemIndex,
                        selectExists.map { it.copy(itemProperties) }
                    )
                }) {
                    Icon(EvaIcons.Outline.Copy, null)
                }
            }

            val isSelectionsValid = state.isSelectionsValid(itemProperties)
            if (state.selectModified.isNotEmpty()) {
                IconButton({
                    if (isSelectionsValid) {
                        onSave()
                    }
                }) {
                    Icon(
                        EvaIcons.Outline.Save, null, tint = if (isSelectionsValid) {
                        LocalContentColor.current
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    )
                }
            }

            if (selectExists.isNotEmpty()) {
                if (update) {
                    IconButton({
                        state.editSelections(itemProperties)
                    }) {
                        if (state.isSelectAllEdit) {
                            Icon(
                                EvaIcons.Fill.Close, null, tint = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Icon(EvaIcons.Outline.Edit, null)
                        }
                    }
                }

                if (delete) {
                    IconButton(onDelete) {
                        Icon(imageVector = EvaIcons.Outline.Trash2, contentDescription = null)
                    }
                }
            }

            if (add) {
                IconButton({
                    state.rowItems.add(
                        lazyListState.firstVisibleItemIndex,
                        RowItem.invoke(uuid4(), createItem!!(), itemProperties)
                    )
                }) {
                    Icon(EvaIcons.Outline.Plus, null)
                }

                if (selectAdds.isNotEmpty()) {
                    IconButton({
                        state.delete(selectAdds)
                    }) {
                        Icon(EvaIcons.Outline.Minus, null)
                    }
                }
            }
        }
    }
}
