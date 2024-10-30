package ai.tech.core.presentation.event.navigator

import ai.tech.core.misc.type.multiple.toLaunchedEffect
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

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

public class DefaultNavigator<T : Any>(override val startDestination: T) : Navigator<T> {
    private val navigationActions =
        MutableSharedFlow<NavigationAction>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    override fun navigate(navAction: NavigationAction): Boolean = navigationActions.tryEmit(navAction)

    override fun navigateBack(): Boolean = navigate(NavigationAction.NavigateBack)

    override fun navigate(route: String): Boolean = navigate(NavigationAction.Navigation.Navigate(route))

    override fun navigate(route: T): Boolean = navigate(NavigationAction.SafeNavigation.Navigate(route))

    override fun navigateBackTo(route: String, inclusive: Boolean, saveState: Boolean): Boolean =
        navigate(NavigationAction.Navigation.NavigateBackTo(route, inclusive, saveState))

    override fun navigateBackTo(route: T, inclusive: Boolean, saveState: Boolean): Boolean =
        navigate(NavigationAction.SafeNavigation.NavigateBackTo(route, inclusive, saveState))

    override fun navigateAndClear(route: String): Boolean =
        navigate(NavigationAction.Navigation.NavigateAndClearTop(route))

    override fun navigateAndClear(route: T): Boolean =
        navigate(NavigationAction.SafeNavigation.NavigateAndClearTop(route))

    override fun navigateAndClearCurrent(route: String): Boolean =
        navigate(NavigationAction.Navigation.NavigateAndClearCurrent(route))

    override fun navigateAndClearCurrent(route: T): Boolean =
        navigate(NavigationAction.SafeNavigation.NavigateAndClearCurrent(route))

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    override fun handleAction(navController: NavHostController): Unit = navigationActions.toLaunchedEffect { action ->
        when (action) {
            NavigationAction.NavigateBack -> navController.navigateUp()

            is NavigationAction.Navigation.Navigate -> navController.navigate(
                action.route,
                navigateNavOptionsBuilder(navController)
            )

            is NavigationAction.SafeNavigation.Navigate<*> -> navController.navigate(
                action.route,
                navigateNavOptionsBuilder(navController)
            )

            is NavigationAction.Navigation.NavigateBackTo -> navController.navigateBackTo(
                action.route,
                action.inclusive,
                action.saveState
            )

            is NavigationAction.SafeNavigation.NavigateBackTo<*> -> navController.navigateBackTo(
                action.route,
                action.inclusive,
                action.saveState
            )

            is NavigationAction.Navigation.NavigateAndClearCurrent -> navController.navigate(
                action.route,
                navigateAndClearCurrentNavOptionsBuilder(navController)
            )

            is NavigationAction.SafeNavigation.NavigateAndClearCurrent<*> -> navController.navigate(
                action.route,
                navigateAndClearCurrentNavOptionsBuilder(navController)
            )

            is NavigationAction.Navigation.NavigateAndClearTop -> navController.navigateAndReplaceStartRoute(action.route)

            is NavigationAction.SafeNavigation.NavigateAndClearTop<*> ->
                navController.navigateAndReplaceStartRoute(action.route)
        }

        navigationActions.resetReplayCache()
    }

    public companion object {
        private fun navigateNavOptionsBuilder(navController: NavHostController): NavOptionsBuilder.() -> Unit = {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
//            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
//            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        private fun navigateAndClearCurrentNavOptionsBuilder(navController: NavHostController): NavOptionsBuilder.() -> Unit =
            {
                navController.currentBackStackEntry?.destination?.route?.let {
                    popUpTo(it) { inclusive = true }
                }
            }
    }
}

private fun NavHostController.navigateAndReplaceStartRoute(startDestRoute: String) {
//    popBackStack(graph.startDestinationId, true)
    graph.setStartDestination(startDestRoute)
    navigate(startDestRoute)
}

private fun <T : Any> NavHostController.navigateAndReplaceStartRoute(startDestRoute: T) {
//    popBackStack(graph.startDestinationId, true)
    graph.setStartDestination(startDestRoute)
    navigate(startDestRoute)
}

private fun NavHostController.navigateBackTo(
    route: String,
    inclusive: Boolean = false,
    saveState: Boolean
): Boolean =
    popBackStack(route, inclusive, saveState)

private fun <T : Any> NavHostController.navigateBackTo(
    route: T,
    inclusive: Boolean = false,
    saveState: Boolean
): Boolean =
    popBackStack(route, inclusive, saveState)
