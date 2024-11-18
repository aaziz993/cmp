package ai.tech.core.presentation.component.textfield.search

import ai.tech.core.misc.type.single.temporalPickerStateToTemporal
import ai.tech.core.presentation.component.textfield.AdvancedTextField
import ai.tech.core.presentation.component.textfield.model.TextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.EqualsSolid
import compose.icons.lineawesomeicons.GreaterThanEqualSolid
import compose.icons.lineawesomeicons.GreaterThanSolid
import compose.icons.lineawesomeicons.LessThanEqualSolid
import compose.icons.lineawesomeicons.LessThanSolid
import compose.icons.lineawesomeicons.MinusSolid
import compose.icons.lineawesomeicons.NotEqualSolid
import io.github.aaziz993.cmp.lib.cmp_lib.generated.resources.Res
import io.github.aaziz993.cmp.lib.cmp_lib.generated.resources.case_sensitive
import io.github.aaziz993.cmp.lib.cmp_lib.generated.resources.regex
import io.github.aaziz993.cmp.lib.cmp_lib.generated.resources.whole_word
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.painterResource
import ai.tech.core.data.validator.Validator
import ai.tech.core.misc.type.single.color
import ai.tech.core.misc.type.single.now
import ai.tech.core.misc.type.single.toEpochMilliseconds
import ai.tech.core.presentation.component.dialog.temporal.AdvancedTemporalPickerDialog
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldCompare
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun AdvancedSearchField(
    state: SearchFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
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
    clearable: Boolean = true,
    outlined: Boolean = false,
    validator: Validator? = null,
    onValidation: (List<String>) -> String = { it.joinToString(", ") },
    showValue: Boolean = true,
    onShowValueChange: ((Boolean) -> Unit)? = null,
    caseMatcher: Boolean = true,
    wordMatcher: Boolean = true,
    regexMatcher: Boolean = true,
    compareMatchers: List<SearchFieldCompare> = listOf(
        SearchFieldCompare.EQUALS,
        SearchFieldCompare.LESS_THAN_EQUAL,
        SearchFieldCompare.LESS_THAN,
        SearchFieldCompare.BETWEEN,
        SearchFieldCompare.NOT_EQUAL,
        SearchFieldCompare.GREATER_THAN,
        SearchFieldCompare.GREATER_THAN_EQUAL,
    ),
): Unit = AdvancedTextField(
    state.query,
    { state.query = it },
    modifier,
    enabled,
    false,
    textStyle,
    label,
    placeholder,
    leadingIcon,
    {
        val isTemporal = type is TextField.LocalTime || type is TextField.LocalDate || type is TextField.LocalDateTime

        val isEnum = type is TextField.Enum

        if (state.compareMatch == SearchFieldCompare.BETWEEN && isTemporal) {
            var showTemporalPicker by remember { mutableStateOf(false) }

            if (showTemporalPicker) {

                var localDate: LocalDate? = null
                var localTime: LocalTime? = null

                when (type) {
                    TextField.LocalTime -> localTime =
                        state.query.ifEmpty { null }?.let(LocalTime::parse) ?: LocalTime.now(
                            TimeZone.currentSystemDefault(),
                        )

                    TextField.LocalDate -> localDate =
                        state.query.ifEmpty { null }?.let(LocalDate::parse) ?: LocalDate.now(
                            TimeZone.currentSystemDefault(),
                        )

                    TextField.LocalDateTime -> state.query.ifEmpty { null }?.let(LocalDateTime::parse).let {
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
                        if (state.query.isNotEmpty()) {
                            temporalPickerStateToTemporal(
                                datePickerState, timePickerState,
                            )?.let { state.query = "${state.query.substringBefore("..")}..$it" }
                        }
                    },
                    onCancel = { showTemporalPicker = false },
                )
            }
            Icon(
                Icons.Default.DateRange,
                "Select date",
                iconModifier.clickable { showTemporalPicker = !showTemporalPicker },
            )
        }

        if (!(isTemporal || isEnum)) {
            if (caseMatcher) {
                Icon(
                    painterResource(Res.drawable.case_sensitive),
                    null,
                    iconModifier.clickable { state.caseMatch = !state.caseMatch },
                    color(!state.caseMatch),
                )
            }

            if (wordMatcher) {
                Icon(
                    painterResource(Res.drawable.whole_word),
                    null,
                    iconModifier.clickable { state.wordMatch = !state.wordMatch },
                    color(!state.wordMatch),
                )
            }

            if (regexMatcher) {
                Icon(
                    painterResource(Res.drawable.regex),
                    null,
                    iconModifier.clickable { state.regexMatch = !state.regexMatch },
                    color(!state.regexMatch),
                )
            }
        }

        if (!isEnum && compareMatchers.isNotEmpty()) {
            var index by remember { mutableStateOf(0) }

            Icon(
                when (state.compareMatch) {
                    SearchFieldCompare.BETWEEN -> LineAwesomeIcons.MinusSolid
                    SearchFieldCompare.LESS_THAN -> LineAwesomeIcons.LessThanSolid
                    SearchFieldCompare.LESS_THAN_EQUAL -> LineAwesomeIcons.LessThanEqualSolid
                    SearchFieldCompare.EQUALS -> LineAwesomeIcons.EqualsSolid
                    SearchFieldCompare.GREATER_THAN_EQUAL -> LineAwesomeIcons.GreaterThanEqualSolid
                    SearchFieldCompare.GREATER_THAN -> LineAwesomeIcons.GreaterThanSolid
                    SearchFieldCompare.NOT_EQUAL -> LineAwesomeIcons.NotEqualSolid
                },
                null,
                iconModifier.clickable {
                    if (++index >= compareMatchers.size) {
                        if (state.compareMatch == SearchFieldCompare.BETWEEN) {
                            state.query = ""
                        }
                        state.compareMatch = compareMatchers[0]
                        index = 0
                    }
                    else {
                        state.compareMatch = compareMatchers[index]
                    }
                },
            )
        }

        trailingIcon?.invoke(it)
    },
    prefix,
    suffix,
    supportingText,
    isError,
    visualTransformation,
    keyboardOptions,
    keyboardActions,
    singleLine,
    maxLines,
    minLines,
    interactionSource,
    shape,
    colors,
    iconModifier,
    type,
    true,
    clearable,
    outlined,
    validator,
    onValidation,
    false,
    showValue,
    onShowValueChange,
)
