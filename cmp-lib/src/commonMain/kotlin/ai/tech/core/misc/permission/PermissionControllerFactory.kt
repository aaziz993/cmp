package ai.tech.core.misc.permission

import androidx.compose.runtime.Composable

public fun interface PermissionControllerFactory {
    public fun createPermissionsController(): PermissionController
}

@Composable
public expect fun rememberPermissionControllerFactory(): PermissionControllerFactory
