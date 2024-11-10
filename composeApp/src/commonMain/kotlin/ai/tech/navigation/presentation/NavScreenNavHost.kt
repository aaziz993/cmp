package ai.tech.navigation.presentation

import ai.tech.home.about.AboutScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import ai.tech.auth.forgotpassword.presentation.ForgotPasswordScreen
import ai.tech.auth.login.presentation.LoginScreen
import ai.tech.auth.profile.presentation.ProfileScreen
import ai.tech.core.misc.di.koinViewModel
import ai.tech.core.presentation.event.navigator.NavigationAction
import ai.tech.core.presentation.navigation.Destination
import ai.tech.home.main.MainScreen
import ai.tech.home.map.MapScreen
import ai.tech.home.settings.SettingsScreen
import ai.tech.navigation.presentation.viewmodel.NavViewModel
import ai.tech.wallet.balance.BalanceScreen
import ai.tech.wallet.crypto.CryptoScreen
import ai.tech.wallet.stock.StockScreen
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Composable
public fun NavScreenNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    route: KClass<*>? = null,
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    enterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            fadeIn(animationSpec = tween(700))
        },
    exitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            fadeOut(animationSpec = tween(700))
        },
    popEnterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        enterTransition,
    popExitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        exitTransition,
    sizeTransform:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
): Unit =
    NavHost(
        navController,
        startDestination,
        modifier,
        contentAlignment,
        route,
        typeMap,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        sizeTransform,
    ) {

        composable<Destination.HomeGraph.Main>(
            Destination.HomeGraph.Main.typeMap,
            Destination.HomeGraph.Main.deepLinks,
        ) {
            val navViewModel =
                koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            MainScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                navViewModel.action(NavigationAction.NavigateBack)
            }
        }

        composable<Destination.HomeGraph.Map>(
            Destination.HomeGraph.Map.typeMap,
            Destination.HomeGraph.Map.deepLinks,
        ) {
            val navViewModel =
                koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            MapScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                navViewModel.action(NavigationAction.NavigateBack)
            }
        }

        composable<Destination.HomeGraph.Settings>(
            Destination.HomeGraph.Settings.typeMap,
            Destination.HomeGraph.Settings.deepLinks,
        ) {
            val navViewModel =
                koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            SettingsScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                navViewModel.action(NavigationAction.NavigateBack)
            }
        }

        composable<Destination.HomeGraph.About>(
            Destination.HomeGraph.About.typeMap,
            Destination.HomeGraph.About.deepLinks,
        ) {
            val navViewModel =
                koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            AboutScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                navViewModel.action(NavigationAction.NavigateBack)
            }
        }

        navigation<Destination.AuthGraph>(Destination.Login) {
            composable<Destination.Login>(
                Destination.Login.typeMap,
                Destination.Login.deepLinks,
            ) {
                val navViewModel =
                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                LoginScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                    navViewModel.action(NavigationAction.NavigateBack)
                }
            }

            composable<Destination.ForgotPassword>(
                Destination.ForgotPassword.typeMap,
                Destination.ForgotPassword.deepLinks,
            ) {
                val navViewModel =
                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                ForgotPasswordScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                    navViewModel.action(NavigationAction.NavigateBack)
                }
            }

            composable<Destination.Profile>(
                Destination.Profile.typeMap,
                Destination.Profile.deepLinks,
            ) {
                val navViewModel =
                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                ProfileScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                    navViewModel.action(NavigationAction.NavigateBack)
                }
            }
        }

        navigation<Destination.WalletGraph>(Destination.WalletGraph.Balance) {
            composable<Destination.WalletGraph.Balance>(
                Destination.WalletGraph.Balance.typeMap,
                Destination.WalletGraph.Balance.deepLinks,
            ) {
                val navViewModel =
                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                BalanceScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                    navViewModel.action(NavigationAction.NavigateBack)
                }
            }

            composable<Destination.WalletGraph.Crypto>(
                Destination.WalletGraph.Crypto.typeMap,
                Destination.WalletGraph.Crypto.deepLinks,
            ) {
                val navViewModel =
                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                CryptoScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                    navViewModel.action(NavigationAction.NavigateBack)
                }
            }

            composable<Destination.WalletGraph.Stock>(
                Destination.WalletGraph.Stock.typeMap,
                Destination.WalletGraph.Stock.deepLinks,
            ) {
                val navViewModel =
                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                StockScreen({ navViewModel.action(NavigationAction.SafeNavigation.Navigate(it)) }) {
                    navViewModel.action(NavigationAction.NavigateBack)
                }
            }
        }
    }
