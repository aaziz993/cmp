package ai.tech.navigation.presentation

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

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
                    popUpTo(navController.graph.startDestinationRoute!!) {
                        saveState = true
                    }

                    // Avoid multiple copies of the same destination when
                    // re-selecting the same item
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },
            { navItem.calculateIcon(selected) },
            navItem.calculateModifier(selected),
            label = { navItem.calculateText(selected) },
            alwaysShowLabel = navItem.alwaysShowLabel,
            badge = { navItem.calculateBadge(selected) },
        )
    }
}
