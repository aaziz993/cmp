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
import androidx.navigation.toRoute
import ai.tech.auth.forgotpassword.presentation.ForgotPasswordScreen
import ai.tech.auth.login.presentation.LoginScreen
import ai.tech.auth.profile.presentation.ProfileScreen
import ai.tech.auth.register.presentation.RegisterScreen
import ai.tech.customs.cms.camera.presentation.CameraScreen
import ai.tech.customs.cms.scales.presentation.ScalesScreen
import ai.tech.customs.cms.xray.presentation.XrayScreen
import ai.tech.home.main.MainScreen
import ai.tech.home.map.MapScreen
import ai.tech.home.settings.SettingsScreen
import ai.tech.wallet.balance.BalanceScreen
import ai.tech.wallet.crypto.CryptoScreen
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
            deepLinks = Destination.HomeGraph.Main.deepLinks
        ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            MainScreen({

            }) {

            }
        }

        composable<Destination.HomeGraph.Map>(
            deepLinks = Destination.HomeGraph.Map.deepLinks
        ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            MapScreen({

            }) {

            }
        }

        composable<Destination.HomeGraph.Settings>(
            deepLinks = Destination.HomeGraph.Settings.deepLinks
        ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            SettingsScreen({

            }) {

            }
        }

        composable<Destination.HomeGraph.About>(
            deepLinks = Destination.HomeGraph.About.deepLinks
        ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

            AboutScreen({

            }) {

            }
        }

        navigation<Destination.AuthGraph>(Destination.Login) {
            composable<Destination.Login>(
                deepLinks = Destination.Login.deepLinks
            ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                Button(onClick = {
                    navController.navigate("calendar") {
                        popUpTo(Destination.AuthGraph) {
                            inclusive = true
                        }
                    }
                }) {

                }

                LoginScreen({

                }) {

                }
            }

            composable<Destination.ForgotPassword>(
                deepLinks = Destination.ForgotPassword.deepLinks
            ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                ForgotPasswordScreen({

                }) {

                }
            }

            composable<Destination.Profile>(
                deepLinks = Destination.Profile.deepLinks
            ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                ProfileScreen({

                }) {

                }
            }
        }

        navigation<Destination.WalletGraph>(Destination.WalletGraph.Balance) {
            composable<Destination.WalletGraph.Balance>(
                deepLinks = Destination.WalletGraph.Balance.deepLinks
            ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                BalanceScreen({

                }) {

                }
            }

            composable<Destination.WalletGraph.Crypto>(
                deepLinks = Destination.WalletGraph.Crypto.deepLinks
            ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                CryptoScreen({

                }) {

                }
            }

            composable<Destination.WalletGraph.Stock>(
                deepLinks = Destination.WalletGraph.Stock.deepLinks
            ) {
//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                CryptoScreen({

                }) {

                }
            }
        }

        navigation<Destination.CustomsGraph>(Destination.CustomsGraph.Document) {
            composable<Destination.CustomsGraph.Camera>(
                deepLinks = Destination.CustomsGraph.Camera.deepLinks
            ) {
                val destination = it.toRoute<Destination.CustomsGraph.Camera>()

//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                CameraScreen({

                }) {

                }
            }

            composable<Destination.CustomsGraph.Xray>(
                deepLinks = Destination.CustomsGraph.Xray.deepLinks
            ) {
                val destination = it.toRoute<Destination.CustomsGraph.Xray>()

//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                XrayScreen({

                }) {

                }
            }

            composable<Destination.CustomsGraph.Scales>(
                deepLinks = Destination.CustomsGraph.Scales.deepLinks
            ) {
                val destination = it.toRoute<Destination.CustomsGraph.Scales>()

//                val navViewModel =
//                    koinViewModel<Destination, NavViewModel>(navController = navController, backStackEntry = it)

                ScalesScreen({

                }) {

                }
            }

            composable<Destination.CustomsGraph.Document>(
                deepLinks = Destination.CustomsGraph.Document.deepLinks
            ) {

            }
        }
    }
