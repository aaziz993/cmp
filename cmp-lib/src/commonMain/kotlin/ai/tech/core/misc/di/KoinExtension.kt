package ai.tech.core.misc.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import org.koin.compose.currentKoinScope
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.viewmodel.defaultExtras

/**
 * [parentBackStackEntry] is the destination to which the [ViewModel] will be scoped to. It is done so by getting the backStackEntry
 * from the backstack by looking up its route.
 */
@Composable
public inline fun <reified VM : ViewModel> koinViewModel(
    qualifier: Qualifier? = null,
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    parentBackStackEntry: NavBackStackEntry,
    key: String? = null,
    extras: CreationExtras? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): VM {
    val parentEntry = remember(backStackEntry) {
        parentBackStackEntry
    }
    return koinViewModel(
        qualifier,
        parentEntry,
        key,
        extras ?: defaultExtras(parentEntry),
        scope,
        parameters,
    )
}

/**
 * [Dest] is the destination to which the [ViewModel] will be scoped to. It is done so by getting the backStackEntry
 * from the backstack by looking up its route.
 */
@Composable
public inline fun <reified Dest : Any, reified VM : ViewModel> koinViewModel(
    qualifier: Qualifier? = null,
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    key: String? = null,
    extras: CreationExtras? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): VM = koinViewModel(
    qualifier,
    navController,
    backStackEntry,
    navController.getBackStackEntry<Dest>(),
    key,
    extras,
    scope,
    parameters,
)

/**
 * [route] is the destination to which the [ViewModel] will be scoped to. It is done so by getting the backStackEntry
 * from the backstack by looking up its route.
 */
@Composable
public inline fun <T : Any, reified VM : ViewModel> koinViewModel(
    qualifier: Qualifier? = null,
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    route: T,
    key: String? = null,
    extras: CreationExtras? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): VM = koinViewModel(
    qualifier,
    navController,
    backStackEntry,
    navController.getBackStackEntry(route),
    key,
    extras,
    scope,
    parameters,
)

/**
 * [route] is the destination to which the [ViewModel] will be scoped to. It is done so by getting the backStackEntry
 * from the backstack by looking up its route.
 */
@Composable
public inline fun <reified VM : ViewModel> koinViewModel(
    qualifier: Qualifier? = null,
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    route: String,
    key: String? = null,
    extras: CreationExtras? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): VM = koinViewModel(
    qualifier,
    navController,
    backStackEntry,
    navController.getBackStackEntry(route),
    key,
    extras,
    scope,
    parameters,
)
