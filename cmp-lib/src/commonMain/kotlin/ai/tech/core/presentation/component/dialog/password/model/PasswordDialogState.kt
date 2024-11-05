package ai.tech.core.presentation.component.dialog.password.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

public class PasswordDialogState(
    password: String = "",
    showPassword: Boolean = false,
) {
    public var password: String by mutableStateOf(password)

    public var showPassword: Boolean by mutableStateOf(showPassword)


    public companion object {
        public val Saver: Saver<PasswordDialogState, *> = listSaver(
            save = { listOf(it.password, it.showPassword) },
            restore = {
                PasswordDialogState(it[0] as String, it[1] as Boolean)
            }
        )
    }
}

@Composable
public fun rememberPasswordDialogState(
    state: PasswordDialogState = PasswordDialogState()
): PasswordDialogState = rememberSaveable(saver = PasswordDialogState.Saver) {
    state
}
