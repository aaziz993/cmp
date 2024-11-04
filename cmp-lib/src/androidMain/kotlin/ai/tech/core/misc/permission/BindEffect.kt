package ai.tech.core.misc.permission

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Suppress("FunctionNaming")
@Composable
public actual fun BindEffect(permissionController: PermissionController) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context: Context = LocalContext.current

    LaunchedEffect(permissionController, lifecycleOwner, context) {
        val activity: ComponentActivity = checkNotNull(context as? ComponentActivity) {
            "$context context is not instance of ComponentActivity"
        }
        permissionController.bind(activity)
    }
}
