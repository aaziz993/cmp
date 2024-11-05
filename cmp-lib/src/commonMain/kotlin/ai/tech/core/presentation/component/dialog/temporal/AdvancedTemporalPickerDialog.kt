package ai.tech.core.presentation.component.dialog.temporal

import ai.tech.core.misc.type.single.localDate
import ai.tech.core.misc.type.single.localTime
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Checkmark
import compose.icons.evaicons.outline.Close
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun AdvancedTemporalPickerDialog(
    datePickerState: DatePickerState? = null,
    timePickerState: TimePickerState? = null,
    title: String? = null,
    showModeToggle: Boolean = true,
    onConfirm: (LocalDate?, LocalTime?) -> Unit,
    onCancel: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = onCancel ?: {}
) {
    var showDial by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest,
        DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            Modifier.fillMaxSize(.8f).background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.extraLarge),
            MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
        ) {
            Column(
                Modifier.fillMaxSize().padding(24.dp), Arrangement.SpaceBetween, Alignment.CenterHorizontally
            ) {
                title?.let {
                    Text(
                        it,
                        Modifier.padding(bottom = 20.dp),
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row(Modifier.fillMaxWidth().wrapContentHeight(), Arrangement.SpaceAround) {
                    datePickerState?.let {
                        Column(
                            Modifier.weight(1f),
                            Arrangement.SpaceAround,
                            Alignment.CenterHorizontally
                        ) {
                            DatePicker(it, title = null, headline = null, showModeToggle = showModeToggle)
                        }
                    }
                    timePickerState?.let {
                        Column(
                            Modifier.weight(1f),
                            Arrangement.SpaceAround,
                            Alignment.CenterHorizontally
                        ) {
                            if (showDial) {
                                TimePicker(it)
                            } else {
                                TimeInput(it)
                            }
                            if (showModeToggle) {
                                IconButton({ showDial = !showDial }) {
                                    Icon(
                                        if (showDial) {
                                            Icons.Filled.Edit
                                        } else {
                                            Icons.Filled.DateRange
                                        },
                                        "Time picker type toggle",
                                    )
                                }
                            }
                        }
                    }
                }
                Row(Modifier.wrapContentSize()) {
                    onCancel?.let {
                        IconButton(it) {
                            Icon(
                                EvaIcons.Outline.Close, null, tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton({
                        onConfirm(datePickerState?.localDate, timePickerState?.localTime)
                        onDismissRequest()
                    }) { Icon(EvaIcons.Outline.Checkmark, null) }
                }
            }
        }
    }
}
