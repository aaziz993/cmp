package ai.tech.core.presentation.event.navigator

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

public interface Navigator<T : Any> {
    public val startDestination: T

    public fun navigate(navAction: NavigationAction): Boolean

    public fun navigateBack(): Boolean

    public fun navigate(route: String): Boolean

    public fun navigate(route: T): Boolean

    public fun navigateBackTo(route: String, inclusive: Boolean = false, saveState: Boolean = false): Boolean

    public fun navigateBackTo(route: T, inclusive: Boolean = false, saveState: Boolean = false): Boolean

    public fun navigateAndClear(route: String): Boolean

    public fun navigateAndClear(route: T): Boolean

    public fun navigateAndClearCurrent(route: String): Boolean

    public fun navigateAndClearCurrent(route: T): Boolean

    @Composable
    public fun handleAction(navController: NavHostController)
}
