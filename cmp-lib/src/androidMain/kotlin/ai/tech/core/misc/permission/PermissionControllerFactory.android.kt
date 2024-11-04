package ai.tech.core.misc.permission

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
public actual fun rememberPermissionControllerFactory(): PermissionControllerFactory {
    val context: Context = LocalContext.current
    return remember(context) {
        PermissionControllerFactory {
            PermissionController(context.applicationContext)
        }
    }
}
