package ai.tech.core.misc.permission

import androidx.compose.runtime.Composable
@Composable
public actual fun rememberPermissionControllerFactory(): PermissionControllerFactory =
    PermissionControllerFactory { PermissionController() }
