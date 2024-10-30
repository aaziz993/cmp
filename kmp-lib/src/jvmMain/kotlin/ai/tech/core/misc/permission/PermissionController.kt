package ai.tech.core.misc.permission

import ai.tech.core.misc.permission.model.PermissionStateType
import ai.tech.core.misc.permission.model.PermissionType

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual class PermissionController {
    public actual suspend fun getPermissionState(permission: PermissionType): PermissionStateType =
        PermissionStateType.GRANTED

    public actual suspend fun getPermissions(permission: PermissionType): Unit = Unit

    public actual fun openAppSettings(): Unit = Unit
}