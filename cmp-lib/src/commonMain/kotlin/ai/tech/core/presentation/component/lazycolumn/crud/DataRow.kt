package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.EntityItem
import ai.tech.core.misc.type.multiple.all
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnState
import ai.tech.core.presentation.component.textfield.AdvancedTextField
import ai.tech.core.presentation.component.textfield.model.TextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Close
import compose.icons.evaicons.outline.Copy
import compose.icons.evaicons.outline.Edit
import compose.icons.evaicons.outline.Minus
import compose.icons.evaicons.outline.Save
import compose.icons.evaicons.outline.Trash2

@Composable
internal fun <T : Any> DataRow(
    state: CRUDLazyColumnState,
    readOnly: Boolean,
    downloadIcon: @Composable () -> Unit,
    properties: List<EntityProperty>,
    item: EntityItem<T>,
    onDownload: ((T) -> Unit)?,
    onNewFrom: (EntityItem<T>) -> Unit,
    onRemove: (id: Any) -> Unit,
    onEdit: () -> Unit,
    onSave: (EntityItem<T>) -> Unit,
    onDelete: (id: Any) -> Unit,
    onSelect: (EntityItem<T>) -> Unit,
    onValueChange: (id: Any, index: Int, value: String) -> Unit,
) = Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    if (state.showSelect) {
        Checkbox(item.isSelected, { onSelect(item) })
    }

    val validations: MutableList<Boolean> = rememberSaveable { item.values.map { true }.toMutableStateList() }

    item.values.forEachIndexed { index, value ->
        val property = properties[index]

        when (val textField = TextField(property.descriptor)) {
            TextField.Text -> AdvancedTextField(
                value?.toString().orEmpty(),
                { onValueChange(item.id, index, it) },
                Modifier.weight(1f).padding(4.dp),
                readOnly = readOnly || property.isReadOnly || !item.isModify,
                singleLine = true,
                outlined = true,
                validator = property.validator,
                onValidation = {
                    validations[index] = it.isEmpty()
                    ""
                },
                showValidationMessage = false,
            )

            else -> AdvancedTextField(
                value?.toString().orEmpty(),
                { onValueChange(item.id, index, it) },
                Modifier.weight(1f).padding(4.dp),
                readOnly = readOnly || property.isReadOnly || !item.isModify,
                singleLine = true,
                type = textField,
                outlined = true,
            )
        }
    }

    if (state.showActions) {
        Row(Modifier.weight(.4f), Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.wrapContentWidth()) {
                if (!item.isNew && onDownload != null) {
                    IconButton({ onDownload(item.entity) }, Modifier.weight(1f), content = downloadIcon)
                }

                if (!readOnly) {
                    IconButton({ onNewFrom(item) }, Modifier.weight(1f)) { Icon(EvaIcons.Outline.Copy, null) }

                    val isValuesValid = validations.all

                    IconButton(
                        {
                            if (isValuesValid) {
                                onSave(item)
                            }
                        },
                        Modifier.weight(1f),
                    ) {
                        Icon(
                            EvaIcons.Outline.Save, null,
                            tint = if (isValuesValid) {
                                LocalContentColor.current
                            }
                            else {
                                MaterialTheme.colorScheme.error
                            },
                        )
                    }

                    if (item.isNew) {
                        IconButton({ onRemove(item.id) }, Modifier.weight(1f)) { Icon(EvaIcons.Outline.Minus, null) }
                    }
                    else {
                        IconButton(onEdit, Modifier.weight(1f)) {
                            if (item.isEdit) {
                                Icon(EvaIcons.Outline.Close, null, tint = MaterialTheme.colorScheme.error)
                            }
                            else {
                                Icon(EvaIcons.Outline.Edit, null)
                            }
                        }

                        IconButton({ onDelete(item.id) }, Modifier.weight(1f)) {
                            Icon(EvaIcons.Outline.Trash2, null)
                        }
                    }
                }
            }
        }
    }
}
