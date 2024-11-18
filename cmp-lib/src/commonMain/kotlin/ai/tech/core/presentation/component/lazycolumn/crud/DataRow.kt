package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnState
import ai.tech.core.presentation.component.lazycolumn.crud.model.EntityColumn
import ai.tech.core.presentation.component.lazycolumn.crud.model.Item
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
import kotlin.reflect.typeOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
internal fun <T : Any> DataRow(
    state: CRUDLazyColumnState,
    readOnly: Boolean,
    downloadIcon: @Composable () -> Unit,
    properties: List<EntityColumn>,
    item: Item<T>,
    onSelect: () -> Unit,
    onDownload: (() -> Unit)?,
    onCopy: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
) = Row(
    Modifier.fillMaxWidth().animateItem(), verticalAlignment = Alignment.CenterVertically,
) {
    if (state.showSelect) {
        Checkbox(item.isSelected, onSelect)
    }

    val itemValuesValidations: MutableList<Boolean> =
        rememberSaveable { item.values.map { true }.toMutableStateList() }

    item.values.forEachIndexed { index, value ->
        val property = properties[index]

        val textField = when (property.descriptor.primeTypeOrNull) {
            typeOf<LocalTime>() -> TextField.LocalTime
            typeOf<LocalDate>() -> TextField.LocalDate
            typeOf<LocalDateTime>() -> TextField.LocalDateTime
            else -> TextField.Text
        }

        when (textField) {
            TextField.LocalTime, TextField.LocalDate, TextField.LocalDateTime -> {
                AdvancedTextField(
                    value?.toString().orEmpty(),
                    { state.replace(item, index, it) },
                    Modifier.weight(1f).padding(4.dp),
                    readOnly = readOnly || property.isReadOnly || item.readOnly,
                    singleLine = true,
                    type = textField,
                    outlined = true,
                )
            }

            else -> AdvancedTextField(
                value?.toString().orEmpty(),
                { state.replace(item, index, it) },
                Modifier.weight(1f).padding(4.dp),
                readOnly = readOnly || property.isReadOnly || item.readOnly,
                singleLine = true,
                outlined = true,
                validator = property.validator,
                onValidation = {
                    itemValuesValidations[index] = it.isEmpty()
                    ""
                },
                showValidationMessage = false,
            )
        }
    }

    if (state.showActions) {
        Row(Modifier.weight(.4f), Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.wrapContentWidth()) {
                onDownload?.takeIf { !item.isNew }?.let {
                    IconButton(it, Modifier.weight(1f)) { downloadIcon() }
                }

                if (!readOnly) {
                    IconButton(
                        {
                            state.rowItems.add(
                                lazyListState.firstVisibleItemIndex,
                                item.copy(properties),
                            )
                        },
                        Modifier.weight(1f),
                    ) {
                        Icon(
                            EvaIcons.Outline.Copy, null,
                        )
                    }

                    if (!item.readOnly) {
                        val isItemValuesValid = itemValuesValidations.all
                        IconButton(
                            {
                                if (isItemValuesValid) {
                                    onSave()
                                }
                            },
                            Modifier.weight(1f),
                        ) {
                            Icon(
                                EvaIcons.Outline.Save, null,
                                tint = if (isItemValuesValid) {
                                    LocalContentColor.current
                                }
                                else {
                                    MaterialTheme.colorScheme.error
                                },
                            )
                        }
                    }

                    if (item.isNew) {
                        IconButton(
                            {
                                state.delete(item)
                            },
                            Modifier.weight(1f),
                        ) {
                            Icon(EvaIcons.Outline.Minus, null)
                        }
                    }
                    else {
                        if (update) {
                            IconButton(
                                {
                                    state.edit(item, properties)
                                },
                                Modifier.weight(1f),
                            ) {
                                if (item.isEdit) {
                                    Icon(
                                        EvaIcons.Outline.Close, null, tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                                else {
                                    Icon(EvaIcons.Outline.Edit, null)
                                }
                            }
                        }

                        if (delete) {
                            IconButton(onDelete, Modifier.weight(1f)) {
                                Icon(EvaIcons.Outline.Trash2, null)
                            }
                        }
                    }
                }
            }
        }
    }
}
