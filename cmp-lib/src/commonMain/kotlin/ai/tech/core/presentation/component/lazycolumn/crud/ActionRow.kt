package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.EntityColumn
import ai.tech.core.presentation.component.lazycolumn.crud.model.Item
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
import compose.icons.evaicons.outline.Upload

@Composable
internal fun <T : Any> ActionRow(
    contentPadding: PaddingValues,
    readOnly: Boolean,
    downloadAllIcon: @Composable () -> Unit,
    properties: List<EntityColumn>,
    items: List<Item<T>>,
    localization: CRUDTableLocalization,
    onDownloadSelected: (() -> Unit)? = null,
    onUpload: (() -> Unit)? = null,
    onEditSelected: () -> Unit,
    onNew: () -> Unit,
    onCopySelected: () -> Unit,
    onSaveSelected: () -> Unit,
    onDeleteSelected: () -> Unit
) = Row(
    Modifier.padding(contentPadding).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
) {

    val selected = items.filter(Item<T>::isSelected)

    val selectedExistItems = items.any { it.isSelected && !it.isNew }

    if (selectedExistItems && onDownloadSelected != null) {
        IconButton(onDownloadSelected, content = downloadAllIcon)
    }

    if (!readOnly) {
        onUpload?.let { IconButton(it) { Icon(EvaIcons.Outline.Upload, null) } }

        IconButton(onNew) { Icon(EvaIcons.Outline.Plus, null) }

        if (selected.isNotEmpty()) {
            IconButton(onCopySelected) { Icon(EvaIcons.Outline.Copy, null) }
        }

        if (selectedExistItems) {
            val selectedAllEditingItems = items.all { it.isSelected && it.isEditing }

            IconButton(onEditSelected) {
                if (selectedAllEditingItems) {
                    Icon(
                        EvaIcons.Fill.Close, null, tint = MaterialTheme.colorScheme.error,
                    )
                }
                else {
                    Icon(EvaIcons.Outline.Edit, null)
                }
            }
        }

        if (selected.filter(Item<T>::isEditing).all { it.validate(properties) }) {
            IconButton(onSaveSelected) { Icon(EvaIcons.Outline.Save, null) }
        }

        if (selected.isNotEmpty()) {
            IconButton(onDeleteSelected) { Icon(EvaIcons.Outline.Minus, null) }
        }
    }
}
