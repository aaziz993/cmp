package ai.tech.core.misc.permission

import ai.tech.core.misc.permission.exception.PermissionDeniedAlwaysException
import ai.tech.core.misc.permission.exception.PermissionDeniedException
import ai.tech.core.misc.permission.exception.PermissionRequestCanceledException
import ai.tech.core.misc.permission.exception.PermissionUnsupportedException
import ai.tech.core.misc.permission.model.PermissionStateType
import ai.tech.core.misc.permission.model.PermissionType
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException

public abstract class PermissionControllerImpl(
    protected val permissionsController: PermissionsController,
) {
    public suspend fun getPermissionState(permission: PermissionType): PermissionStateType =
        permissionTypeMap[permission]?.let { permissionStateMap[permissionsController.getPermissionState(it)]!! }
            ?: PermissionStateType.UNSUPPORTED

    public suspend fun getPermissions(permission: PermissionType) {
        permissionTypeMap[permission]?.let {
            try {
                permissionsController.providePermission(it)
            } catch (e: DeniedAlwaysException) {
                throw PermissionDeniedAlwaysException(e.message)
            } catch (e: DeniedException) {
                PermissionDeniedException(e.message)
            } catch (e: RequestCanceledException) {
                PermissionRequestCanceledException(e.message)
            }
        } ?: PermissionUnsupportedException
    }

    public fun openAppSettings(): Unit = permissionsController.openAppSettings()
}

public val permissionTypeMap: Map<PermissionType, Permission> =
    mapOf(
        PermissionType.CAMERA to Permission.CAMERA,
        PermissionType.GALLERY to Permission.GALLERY,
        PermissionType.STORAGE to Permission.STORAGE,
        PermissionType.WRITE_STORAGE to Permission.WRITE_STORAGE,
        PermissionType.LOCATION to Permission.LOCATION,
        PermissionType.COARSE_LOCATION to Permission.COARSE_LOCATION,
        PermissionType.BACKGROUND_LOCATION to Permission.BACKGROUND_LOCATION,
        PermissionType.BLUETOOTH_LE to Permission.BLUETOOTH_LE,
        PermissionType.REMOTE_NOTIFICATION to Permission.REMOTE_NOTIFICATION,
        PermissionType.RECORD_AUDIO to Permission.RECORD_AUDIO,
        PermissionType.BLUETOOTH_SCAN to Permission.BLUETOOTH_SCAN,
        PermissionType.BLUETOOTH_ADVERTISE to Permission.BLUETOOTH_ADVERTISE,
        PermissionType.BLUETOOTH_CONNECT to Permission.BLUETOOTH_CONNECT,
        PermissionType.CONTACTS to Permission.CONTACTS,
        PermissionType.SENSORS to Permission.MOTION,
    )

private val permissionStateMap =
    mapOf(
        PermissionState.NotDetermined to PermissionStateType.NOT_DETERMINED,
        PermissionState.NotGranted to PermissionStateType.NOT_GRANTED,
        PermissionState.Granted to PermissionStateType.GRANTED,
        PermissionState.Denied to PermissionStateType.DENIED,
        PermissionState.DeniedAlways to PermissionStateType.DENIED_ALWAYS,
    )
