package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.data.crud.client.model.isEditsSelectedAll
import ai.tech.core.data.crud.client.model.isSelectedAnyExists
import ai.tech.core.data.crud.client.model.isSelectedAnyNews
import ai.tech.core.data.crud.client.model.modifies
import ai.tech.core.data.crud.client.model.selected
import ai.tech.core.data.crud.client.model.validate
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnLocalization
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
internal fun <T : Any> ActionRow(
    contentPadding: PaddingValues,
    readOnly: Boolean,
    downloadAllIcon: @Composable () -> Unit,
    properties: List<EntityProperty>,
    items: List<MutationItem<T>>,
    localization: CRUDLazyColumnLocalization,
    onDownloadSelected: (() -> Unit)?,
    onUpload: (() -> Unit)?,
    onAdd: () -> Unit,
    onCopySelected: () -> Unit,
    onRemoveSelected: () -> Unit,
    onEditSelected: () -> Unit,
    onSaveSelected: () -> Unit,
    onDeleteSelected: () -> Unit
) = Row(
    Modifier.padding(contentPadding).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
) {

    val selected = items.selected

    val isSelectedAnyExists = items.isSelectedAnyExists

    if (isSelectedAnyExists && onDownloadSelected != null) {
        IconButton(onDownloadSelected, content = downloadAllIcon)
    }

    if (!readOnly) {
        onUpload?.let { IconButton(it) { Icon(EvaIcons.Outline.Upload, null) } }

        IconButton(onAdd) { Icon(EvaIcons.Outline.Plus, null) }

        if (selected.isNotEmpty()) {
            IconButton(onCopySelected) { Icon(EvaIcons.Outline.Copy, null) }
        }

        if (isSelectedAnyExists) {
            val isEditsSelectedAll = items.isEditsSelectedAll

            IconButton(onEditSelected) {
                if (isEditsSelectedAll) {
                    Icon(
                        EvaIcons.Fill.Close, null, tint = MaterialTheme.colorScheme.error,
                    )
                }
                else {
                    Icon(EvaIcons.Outline.Edit, null)
                }
            }

            IconButton(onDeleteSelected) { Icon(EvaIcons.Outline.Trash2, null) }
        }

        val isSelectedModifiesValid = selected.modifies.validate(properties)

        if (isSelectedModifiesValid) {
            IconButton(onSaveSelected) { Icon(EvaIcons.Outline.Save, null) }
        }

        if (items.isSelectedAnyNews) {
            IconButton(onRemoveSelected) { Icon(EvaIcons.Outline.Minus, null) }
        }
    }
}
