package ai.tech.core.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import ai.tech.core.presentation.component.model.item.NavigationItem
import androidx.navigation.NavType
import kotlin.reflect.KType
import kotlinx.serialization.Serializable

public sealed interface Destination {
    @Serializable
    public data object HomeGraph : Destination {

        public val deepLinks: List<NavDeepLink>
            get() = Destination.deepLinks.map { navDeepLink<Main>("${it}home") }

        @Serializable
        public data object Main : Destination {

            public val typeMap: kotlin.collections.Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = HomeGraph.deepLinks.map { navDeepLink<Main>("${it}main") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Home, label) },
                selectedIcon = { Icon(Icons.Filled.Home, label) },
                route = Main,
            )
        }

        @Serializable
        public data object Map : Destination {

            public val typeMap: kotlin.collections.Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = HomeGraph.deepLinks.map { navDeepLink<Map>("${it}map") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Map, label) },
                selectedIcon = { Icon(Icons.Filled.Map, label) },
                route = Map,
            )
        }

        @Serializable
        public data object Settings : Destination {

            public val typeMap: kotlin.collections.Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = HomeGraph.deepLinks.map { navDeepLink<Settings>("${it}settings") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Settings, label) },
                selectedIcon = { Icon(Icons.Filled.Settings, label) },
                route = Settings,
            )
        }

        @Serializable
        public data object About : Destination {

            public val typeMap: kotlin.collections.Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = HomeGraph.deepLinks.map { navDeepLink<About>("${it}about") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Info, label) },
                selectedIcon = { Icon(Icons.Filled.Info, label) },
                route = About,
            )
        }
    }

    @Serializable
    public data object AuthGraph : Destination {

        public val typeMap: Map<KType, NavType<*>> = emptyMap()

        public val deepLinks: List<String>
            get() = Destination.deepLinks.map { "${it}auth/" }

        @Serializable
        public data object Login : Destination {

            public val typeMap: Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = AuthGraph.deepLinks.map { navDeepLink<Login>("${it}login") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.AutoMirrored.Outlined.Login, label) },
                selectedIcon = { Icon(Icons.AutoMirrored.Filled.Login, label) },
                route = Login,
            )
        }

        @Serializable
        public data class ForgotPassword(val username: String) : Destination {

            public companion object {

                public val typeMap: Map<KType, NavType<*>> = emptyMap()

                public val deepLinks: List<NavDeepLink> =
                    AuthGraph.deepLinks.map { navDeepLink<ForgotPassword>("${it}forgotpassword") }
            }
        }

        @Serializable
        public data object Profile : Destination {

            public val typeMap: Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = AuthGraph.deepLinks.map { navDeepLink<Profile>("${it}profile") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Person, label) },
                selectedIcon = { Icon(Icons.Filled.Person, label) },
                route = Profile,
            )
        }
    }

    @Serializable
    public data object WalletGraph : Destination {

        public val typeMap: Map<KType, NavType<*>> = emptyMap()

        public val deepLinks: List<String>
            get() = Destination.deepLinks.map { "${it}wallet/" }

        @Serializable
        public data object Balance : Destination {

            public val typeMap: Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = WalletGraph.deepLinks.map { navDeepLink<Balance>("${it}balance") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.AccountBalance, label) },
                selectedIcon = { Icon(Icons.Filled.AccountBalance, label) },
                route = Balance,
            )
        }

        @Serializable
        public data object Crypto : Destination {

            public val typeMap: Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = WalletGraph.deepLinks.map { navDeepLink<Crypto>("${it}crypto") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.EnhancedEncryption, label) },
                selectedIcon = { Icon(Icons.Filled.EnhancedEncryption, label) },
                route = Crypto,
            )
        }

        @Serializable
        public data object Stock : Destination {

            public val typeMap: Map<KType, NavType<*>> = emptyMap()

            public val deepLinks: List<NavDeepLink>
                get() = WalletGraph.deepLinks.map { navDeepLink<Stock>("${it}stock") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.CurrencyExchange, label) },
                selectedIcon = { Icon(Icons.Filled.CurrencyExchange, label) },
                route = Stock,
            )
        }
    }

    public companion object {

        public val deepLinks: List<String> = listOf("https://", "http://")

        public fun navigationItems(
            homeMainLabel: String,
            homeMapLabel: String,
            homeSettingsLabel: String,
            homeAboutLabel: String,
            authLoginLabel: String,
            authProfileLabel: String,
            walletBalanceLabel: String,
            walletCryptoLabel: String,
            walletStockLabel: String,
        ): List<NavigationItem<Destination>> = listOf(
            HomeGraph.Main.navigationItem(homeMainLabel),
            HomeGraph.Map.navigationItem(homeMapLabel),
            HomeGraph.Settings.navigationItem(homeSettingsLabel),
            HomeGraph.About.navigationItem(homeAboutLabel),
            Login.navigationItem(authLoginLabel),
            Profile.navigationItem(authProfileLabel),
            WalletGraph.Balance.navigationItem(walletBalanceLabel),
            WalletGraph.Crypto.navigationItem(walletCryptoLabel),
            WalletGraph.Stock.navigationItem(walletStockLabel),
        )
    }
}
