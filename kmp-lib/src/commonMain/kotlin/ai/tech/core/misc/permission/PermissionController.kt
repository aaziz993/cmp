package ai.tech.core.misc.permission

import core.permission.model.PermissionStateType
import core.permission.model.PermissionType

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public expect class PermissionController {
    public suspend fun getPermissionState(permission: PermissionType): PermissionStateType

    public suspend fun getPermissions(permission: PermissionType)

    public fun openAppSettings()
}

public suspend fun PermissionController.isPermissionGranted(permission: PermissionType): Boolean =
    getPermissionState(permission) == PermissionStateType.GRANTED