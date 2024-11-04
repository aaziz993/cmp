package ai.tech.core.misc.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
public actual fun rememberPermissionControllerFactory(): PermissionControllerFactory {
    return remember {
        PermissionControllerFactory {
            PermissionController()
        }
    }
}
