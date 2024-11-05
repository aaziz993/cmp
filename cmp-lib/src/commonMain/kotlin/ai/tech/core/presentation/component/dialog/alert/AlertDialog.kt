package ai.tech.core.presentation.component.dialog.alert

import ai.tech.core.presentation.component.dialog.alert.model.AlertDialogLocalization
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.AlertTriangle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun AlertDialog(
    message: String,
    iconVector: ImageVector = EvaIcons.Outline.AlertTriangle,
    isError: Boolean = true,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = onCancel ?: {},
    localization: AlertDialogLocalization = AlertDialogLocalization()
): Unit = BasicAlertDialog(
    onDismissRequest
) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Icon(
                iconVector,
                null,
                Modifier.size(64.dp),
                tint = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    LocalContentColor.current
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                message, color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Unspecified
                }
            )

            if (!(onCancel == null && onConfirm == null)) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    onCancel?.let {
                        TextButton(it) {
                            Text(localization.cancel)
                        }
                    }

                    onConfirm?.let {
                        TextButton(it) {
                            Text(localization.confirm)
                        }
                    }
                }
            }
        }
    }
}
