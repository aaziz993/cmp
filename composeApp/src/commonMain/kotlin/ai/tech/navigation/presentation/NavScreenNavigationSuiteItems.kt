package ai.tech.navigation.presentation

import ai.tech.core.presentation.navigation.Destination
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

@Composable
public fun NavScreenNavigationSuiteItems(
    navController: NavHostController,
    currentDestination: NavDestination?,
    homeMainLabel: String,
    homeMapLabel: String,
    homeSettingsLabel: String,
    homeAboutLabel: String,
    authLoginLabel: String,
    authProfileLabel: String,
    walletBalanceLabel: String,
    walletCryptoLabel: String,
    walletStockLabel: String,
): NavigationSuiteScope.() -> Unit = {
    Destination.Companion.navigationItems(
        homeMainLabel,
        homeMapLabel,
        homeSettingsLabel,
        homeAboutLabel,
        authLoginLabel,
        authProfileLabel,
        walletBalanceLabel,
        walletCryptoLabel,
        walletStockLabel,
    ).forEach { navItem ->
        val selected = currentDestination?.hierarchy?.any { it.hasRoute(navItem.route::class) } == true
        item(
            selected,
            {
                navController.navigate(navItem.route) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph. findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            },
            { navItem.calculateIcon(selected) },
            navItem.calculateModifier(selected),
            label = { navItem.calculateText(selected) },
            alwaysShowLabel = navItem.alwaysShowLabel,
            badge = { navItem.calculateBadge(selected) }
        )
    }
}
