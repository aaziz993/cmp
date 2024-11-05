package ai.tech.core.presentation.component.dialog.password.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

public class PasswordResetDialogState(
    password: String = "",
    newPassword: String = "",
    repeatPassword: String = "",
    showPassword: Boolean = false,
) {
    public var password: String by mutableStateOf(password)

    public var newPassword: String by mutableStateOf(newPassword)

    public var repeatPassword: String by mutableStateOf(repeatPassword)

    public var showPassword: Boolean by mutableStateOf(showPassword)


    public companion object {
        public val Saver: Saver<PasswordResetDialogState, *> = listSaver(
            save = { listOf(it.password, it.newPassword, it.repeatPassword, it.showPassword) },
            restore = {
                PasswordResetDialogState(it[0] as String, it[1] as String, it[2] as String, it[3] as Boolean)
            }
        )
    }
}

@Composable
public fun rememberPasswordResetDialogState(
    state: PasswordResetDialogState = PasswordResetDialogState()
): PasswordResetDialogState = rememberSaveable(saver = PasswordResetDialogState.Saver) {
    state
}
