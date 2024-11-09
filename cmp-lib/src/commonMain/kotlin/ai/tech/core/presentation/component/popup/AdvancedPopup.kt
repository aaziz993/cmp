package ai.tech.core.presentation.component.popup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Popup
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Close
import compose.icons.evaicons.outline.Save

@Composable
public fun Popup(
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
    onClose: () -> Unit,
    onDismissRequest: () -> Unit = onClose,
    content: @Composable () -> Unit
): Unit = Popup(Alignment.Center, onDismissRequest = onDismissRequest) {
    Box(modifier) {
        content()
        Row(Modifier.wrapContentSize().align(Alignment.BottomCenter)) {
            IconButton(onClose) {
                Icon(EvaIcons.Outline.Close, null, tint = MaterialTheme.colorScheme.error)
            }
            IconButton(onSave) {
                Icon(EvaIcons.Outline.Save, null)
            }
        }
    }
}
