package ai.tech.core.presentation.component.textfield

import ai.tech.core.data.validator.Validator
import ai.tech.core.misc.type.single.now
import ai.tech.core.misc.type.single.parseOrNull
import ai.tech.core.misc.type.single.toEpochMilliseconds
import ai.tech.core.misc.type.single.temporalPickerStateToTemporal
import ai.tech.core.presentation.component.dialog.temporal.AdvancedTemporalPickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Close
import compose.icons.evaicons.outline.Eye
import compose.icons.evaicons.outline.EyeOff2
import ai.tech.core.presentation.component.textfield.model.TextField
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun AdvancedTextField(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable ((isError: Boolean) -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable ((isError: Boolean) -> Unit)? = null,
    trailingIcon: @Composable ((isError: Boolean) -> Unit)? = null,
    prefix: @Composable ((isError: Boolean) -> Unit)? = null,
    suffix: @Composable ((isError: Boolean) -> Unit)? = null,
    supportingText: @Composable ((isError: Boolean) -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    iconModifier: Modifier = Modifier.padding(horizontal = 2.dp),
    type: TextField = TextField.Text,
    cursor: Boolean = false,
    clearable: Boolean = !readOnly,
    outlined: Boolean = false,
    validator: Validator? = null,
    onValidation: (List<String>) -> String = { it.joinToString(", ") },
    showValidationMessage: Boolean = true,
    showValue: Boolean = true,
    onShowValueChange: ((Boolean) -> Unit)? = null,
) {
    val isTemporal =
        type is TextField.LocalTime || type is TextField.LocalDate || type == TextField.LocalDateTime

    val isUnvalidated = readOnly || isTemporal || validator == null

    val validation = if (isUnvalidated) {
        emptyList()
    }
    else {
        validator!!.validate(value)
    }

    val isErrorWithValidation = isError || validation.isNotEmpty()

    val advancedOnValueChange = if (readOnly || isTemporal || type is TextField.Enum) {
        {}
    }
    else {
        onValueChange
    }

    val advancedLabel: (@Composable () -> Unit)? = label?.let { { it.invoke(isErrorWithValidation) } }

    val advancedLeadingIcon: (@Composable () -> Unit)? = leadingIcon?.let { { it.invoke(isErrorWithValidation) } }

    val clearIconButton: (@Composable () -> Unit)? = if (clearable && enabled && value.isNotBlank()) {
        {
            Icon(
                EvaIcons.Fill.Close, null, iconModifier.clickable { onValueChange("") }, MaterialTheme.colorScheme.error,
            )
        }
    }
    else {
        null
    }

    val showIconButton: (@Composable (isError: Boolean) -> Unit)? = onShowValueChange?.let { osc ->
        {
            Icon(
                if (showValue) {
                    EvaIcons.Outline.EyeOff2
                }
                else {
                    EvaIcons.Outline.Eye
                },
                null, iconModifier.clickable { osc(!showValue) }, iconColorErrorAware(it),
            )
        }
    }

    val temporalIconButton: (@Composable () -> Unit)? = if (isTemporal && !readOnly) {
        var showTemporalPicker by remember { mutableStateOf(false) }

        if (showTemporalPicker) {

            var localDate: LocalDate? = null
            var localTime: LocalTime? = null

            when (type) {
                TextField.LocalTime -> localTime =
                    value.ifEmpty { null }?.let(LocalTime::parseOrNull) ?: LocalTime.now(
                        TimeZone.currentSystemDefault(),
                    )

                TextField.LocalDate -> localDate =
                    value.ifEmpty { null }?.let(LocalDate::parseOrNull) ?: LocalDate.now(
                        TimeZone.currentSystemDefault(),
                    )

                TextField.LocalDateTime -> value.ifEmpty { null }?.let(LocalDateTime::parseOrNull).let {
                    it ?: LocalDateTime.now(
                        TimeZone.currentSystemDefault(),
                    )
                }.let {
                    localTime = it.time
                    localDate = it.date
                }

                else -> {}
            }

            val datePickerState = localDate?.let { rememberDatePickerState(it.toEpochMilliseconds()) }

            val timePickerState = localTime?.let { rememberTimePickerState(it.hour, it.minute, true) }

            AdvancedTemporalPickerDialog(
                datePickerState,
                timePickerState,
                onConfirm = { _, _ ->
                    onValueChange(
                        temporalPickerStateToTemporal(datePickerState, timePickerState)?.toString().orEmpty(),
                    )
                },
                onCancel = { showTemporalPicker = false },
            )
        }
        {
            Icon(
                Icons.Default.DateRange,
                "Select date",
                iconModifier.clickable(onClick = { showTemporalPicker = !showTemporalPicker }),
            )

        }
    }
    else {
        null
    }

    val advancedTrailingIcon: (@Composable () -> Unit)? =
        if (!(clearIconButton == null && showIconButton == null && trailingIcon == null)) {
            {
                Row(Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically) {
                    clearIconButton?.invoke()
                    showIconButton?.invoke(isErrorWithValidation)
                    temporalIconButton?.invoke()
                    trailingIcon?.invoke(isErrorWithValidation)
                }
            }
        }
        else {
            null
        }

    val advancedPrefix: (@Composable () -> Unit)? = prefix?.let { { it.invoke(isErrorWithValidation) } }

    val advancedSuffix: (@Composable () -> Unit)? = suffix?.let { { it.invoke(isErrorWithValidation) } }

    val advancedSupportingText: (@Composable () -> Unit)? = supportingText?.let { { it.invoke(isErrorWithValidation) } }

    val advancedVisualTransformation = visualTransformation ?: if (showValue) {
        VisualTransformation.None
    }
    else {
        PasswordVisualTransformation()
    }

    val textField: @Composable () -> Unit = if (outlined) {
        {
            OutlinedTextField(
                value,
                advancedOnValueChange,
                modifier,
                enabled,
                readOnly && !cursor,
                textStyle,
                advancedLabel,
                placeholder,
                advancedLeadingIcon,
                advancedTrailingIcon,
                advancedPrefix,
                advancedSuffix,
                advancedSupportingText,
                isErrorWithValidation,
                advancedVisualTransformation,
                keyboardOptions,
                keyboardActions,
                singleLine,
                maxLines,
                minLines,
                interactionSource,
                shape,
                colors,
            )
        }
    }
    else {
        {
            TextField(
                value,
                advancedOnValueChange,
                modifier,
                enabled,
                readOnly && !cursor,
                textStyle,
                advancedLabel,
                placeholder,
                advancedLeadingIcon,
                advancedTrailingIcon,
                advancedPrefix,
                advancedSuffix,
                advancedSupportingText,
                isErrorWithValidation,
                advancedVisualTransformation,
                keyboardOptions,
                keyboardActions,
                singleLine,
                maxLines,
                minLines,
                interactionSource,
                shape,
                colors,
            )
        }
    }


    if (!isUnvalidated) {
        onValidation(validation).let {
            if (showValidationMessage) {
                Column(Modifier.wrapContentSize()) {
                    textField()
                    if (validation.isNotEmpty()) {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
                return
            }
        }
    }
    textField()
}

@Composable
private fun iconColorErrorAware(isError: Boolean) = if (isError) {
    MaterialTheme.colorScheme.error
}
else {
    LocalContentColor.current
}
