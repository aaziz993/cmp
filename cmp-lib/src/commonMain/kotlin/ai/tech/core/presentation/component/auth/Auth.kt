package ai.tech.core.presentation.component.auth

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.auth.model.User
import androidx.compose.runtime.Composable

@Composable
public inline fun Resource(
    auth: AuthResource?,
    provider: String?,
    user: User?,
    elseContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit
): Unit = if (auth == null) {
    content()
    elseContent()
} else {
    if ((provider in auth.providers && user?.validate(auth.role) == true)) {
        content()
    } else {
        elseContent()
    }
}
