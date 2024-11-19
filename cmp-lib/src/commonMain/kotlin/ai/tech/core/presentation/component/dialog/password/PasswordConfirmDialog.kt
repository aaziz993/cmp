package ai.tech.core.presentation.component.dialog.password

import ai.tech.core.presentation.component.dialog.password.model.PasswordDialogLocalization
import ai.tech.core.presentation.component.dialog.password.model.PasswordDialogState
import ai.tech.core.presentation.component.dialog.password.model.rememberPasswordDialogState
import ai.tech.core.presentation.component.textfield.AdvancedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Lock

@Composable
public fun PasswordDialog(
    state: PasswordDialogState = rememberPasswordDialogState(),
    icon: (@Composable (isError: Boolean) -> Unit)? = {
        Icon(
            EvaIcons.Outline.Lock, null, tint = if (it) {
                MaterialTheme.colorScheme.error
            } else {
                LocalContentColor.current
            }
        )
    },
    errorMessage: String? = null,
    onSubmit: (password: String) -> Unit,
    onDismissRequest: () -> Unit,
    localization: PasswordDialogLocalization,
): Unit = Dialog(
    onDismissRequest = onDismissRequest,
) {
    val focusRequesters = remember { List(4) { FocusRequester() } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = localization.title,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            AdvancedTextField(
                state.password,
                { state.password = it },
                Modifier.focusRequester(focusRequesters[0]).fillMaxWidth(),
                label = { Text(localization.password) },
                leadingIcon = icon?.let { { it(errorMessage != null) } },
                placeholder = { Text(localization.password) },
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusRequesters[1].requestFocus() }
                ),
                singleLine = true,
                outlined = true,
                showValue = state.showPassword,
                onShowValueChange = { state.showPassword = it },
            )


            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                { onSubmit(state.password) },
                Modifier.focusRequester(focusRequesters[1]),
                state.password.isNotBlank()
            ) {
                Text(localization.submit)
            }

            LaunchedEffect(Unit) {
                focusRequesters[0].requestFocus()
            }
        }
    }
}
