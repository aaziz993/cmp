package ai.tech.core.misc.permission

import android.content.Context
import dev.icerock.moko.permissions.PermissionsController

public actual class PermissionController(applicationContext: Context) :
    PermissionControllerImpl(PermissionsController(applicationContext)) {
    public fun bind(activity: androidx.activity.ComponentActivity) {
        permissionsController.bind(activity)
    }
}