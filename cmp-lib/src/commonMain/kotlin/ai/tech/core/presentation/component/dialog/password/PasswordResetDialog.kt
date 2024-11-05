package ai.tech.core.presentation.component.dialog.password

import ai.tech.core.data.validator.Validator
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
import ai.tech.core.presentation.component.dialog.password.model.PasswordResetDialogLocalization
import ai.tech.core.presentation.component.dialog.password.model.PasswordResetDialogState
import ai.tech.core.presentation.component.dialog.password.model.rememberPasswordResetDialogState
import ai.tech.core.presentation.component.textfield.AdvancedTextField

@Composable
public fun PasswordResetDialog(
    state: PasswordResetDialogState = rememberPasswordResetDialogState(),
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
    validator: Validator? = null,
    onValidate: (List<String>?) -> String? = { it?.joinToString(", ") },
    onSubmit: (password: String, newPassword: String, repeatPassword: String) -> Unit,
    onDismissRequest: () -> Unit,
    localization: PasswordResetDialogLocalization,
): Unit =
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
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

                val validation = onValidate(validator?.validate(state.newPassword))

                val isErrorWithValidation = !(errorMessage == null && validation == null)

                val focusRequesters = remember { List(4) { FocusRequester() } }

                val passwordField: @Composable (
                    label: String,
                    focusRequesterIndex: Int,
                    isError: Boolean,
                    value: String,
                    onValueChange: (String) -> Unit
                ) -> Unit =
                    { label, focusRequesterIndex, isError, value, onValueChange ->
                        AdvancedTextField(
                            value,
                            onValueChange,
                            Modifier.focusRequester(focusRequesters[focusRequesterIndex]).fillMaxWidth(),
                            label = { Text(label) },
                            placeholder = { Text(label) },
                            leadingIcon = icon?.let { { it(isError) } },
                            isError = isError,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { focusRequesters[focusRequesterIndex + 1].requestFocus() }
                            ),
                            singleLine = true,
                            outlined = true,
                            showValue = state.showPassword,
                            onShowValueChange = { state.showPassword = it },
                        )
                    }

                passwordField(
                    localization.password,
                    0,
                    isErrorWithValidation,
                    state.password
                ) {
                    state.password = it
                }

                Spacer(modifier = Modifier.height(8.dp))

                val passwordMismatchError = state.newPassword != state.repeatPassword

                passwordField(
                    localization.newPassword,
                    1,
                    isErrorWithValidation || passwordMismatchError,
                    state.newPassword
                ) {
                    state.newPassword = it
                }

                validation?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(8.dp))

                passwordField(
                    localization.repeatPassword,
                    2,
                    isErrorWithValidation || passwordMismatchError,
                    state.repeatPassword
                ) {
                    state.repeatPassword = it
                }

                if (passwordMismatchError) {
                    Text(localization.passwordMismatch, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    { onSubmit(state.password, state.newPassword, state.repeatPassword) },
                    Modifier.focusRequester(focusRequesters[3]),
                    state.newPassword.isNotBlank() && state.repeatPassword.isNotBlank() && state.newPassword == state.repeatPassword
                ) {
                    Text(localization.submit)
                }

                LaunchedEffect(Unit) {
                    focusRequesters[0].requestFocus()
                }
            }
        }
    }
