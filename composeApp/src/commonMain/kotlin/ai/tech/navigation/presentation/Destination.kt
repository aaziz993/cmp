package ai.tech.navigation.presentation

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
import kotlinx.serialization.Serializable

public sealed interface Destination {
    @Serializable
    public data object HomeGraph : Destination {
        public val deepLinks: List<NavDeepLink>
            get() = Destination.deepLinks.map { navDeepLink<Main>("${it}home") }

        @Serializable
        public data object Main : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.HomeGraph.deepLinks.map { navDeepLink<Main>("${it}main") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Home, label) },
                selectedIcon = { Icon(Icons.Filled.Home, label) },
                route = Main,
            )
        }

        @Serializable
        public data object Map : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.HomeGraph.deepLinks.map { navDeepLink<Map>("${it}map") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Map, label) },
                selectedIcon = { Icon(Icons.Filled.Map, label) },
                route = Map,
            )
        }

        @Serializable
        public data object Settings : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.HomeGraph.deepLinks.map { navDeepLink<Settings>("${it}settings") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Settings, label) },
                selectedIcon = { Icon(Icons.Filled.Settings, label) },
                route = Settings
            )
        }

        @Serializable
        public data object About : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.HomeGraph.deepLinks.map { navDeepLink<About>("${it}about") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Info, label) },
                selectedIcon = { Icon(Icons.Filled.Info, label) },
                route = About
            )
        }
    }

    @Serializable
    public data object AuthGraph : Destination {
        public val deepLinks: List<String>
            get() = Destination.deepLinks.map { "${it}auth/" }
    }

    @Serializable
    public data object Login : Destination {
        public val deepLinks: List<NavDeepLink>
            get() = Destination.AuthGraph.deepLinks.map { navDeepLink<Login>("${it}login") }

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
            public val deepLinks: List<NavDeepLink> =
                Destination.AuthGraph.deepLinks.map { navDeepLink<ForgotPassword>("${it}forgotpassword") }
        }
    }

    @Serializable
    public data object Profile : Destination {
        public val deepLinks: List<NavDeepLink>
            get() = Destination.AuthGraph.deepLinks.map { navDeepLink<Profile>("${it}profile") }

        public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
            text = { Text(label) },
            icon = { Icon(Icons.Outlined.Person, label) },
            selectedIcon = { Icon(Icons.Filled.Person, label) },
            route = Profile,
        )
    }

    @Serializable
    public data object WalletGraph : Destination {
        public val deepLinks: List<String>
            get() = Destination.deepLinks.map { "${it}wallet/" }

        @Serializable
        public data object Balance : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.WalletGraph.deepLinks.map { navDeepLink<Balance>("${it}balance") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.AccountBalance, label) },
                selectedIcon = { Icon(Icons.Filled.AccountBalance, label) },
                route = Balance,
            )
        }

        @Serializable
        public data object Crypto : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.WalletGraph.deepLinks.map { navDeepLink<Crypto>("${it}crypto") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.EnhancedEncryption, label) },
                selectedIcon = { Icon(Icons.Filled.EnhancedEncryption, label) },
                route = Crypto,
            )
        }

        @Serializable
        public data object Stock : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.WalletGraph.deepLinks.map { navDeepLink<Stock>("${it}stock") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.CurrencyExchange, label) },
                selectedIcon = { Icon(Icons.Filled.CurrencyExchange, label) },
                route = Stock,
            )
        }
    }

    @Serializable
    public data object CustomsGraph : Destination {
        public val deepLinks: List<String>
            get() = Destination.deepLinks.map { "${it}customs/" }

        @Serializable
        public data class Camera(val customsCode: String? = null) : Destination {
            public companion object {
                public val deepLinks: List<NavDeepLink> =
                    Destination.CustomsGraph.deepLinks.map { navDeepLink<Camera>("${it}camera") }

                public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                    text = { Text(label) },
                    icon = { Icon(Icons.Outlined.CameraOutdoor, label) },
                    selectedIcon = { Icon(Icons.Filled.CameraOutdoor, label) },
                    route = Camera(),
                )
            }
        }

        @Serializable
        public data class Xray(val customsCode: String? = null) : Destination {
            public companion object {
                public val deepLinks: List<NavDeepLink> =
                    Destination.CustomsGraph.deepLinks.map { navDeepLink<Xray>("${it}xray") }

                public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                    text = { Text(label) },
                    icon = { Icon(Icons.Outlined.Monitor, label) },
                    selectedIcon = { Icon(Icons.Filled.Monitor, label) },
                    route = Xray(),
                )
            }
        }

        @Serializable
        public data class Scales(val customsCode: String? = null) : Destination {
            public companion object {
                public val deepLinks: List<NavDeepLink> =
                    Destination.CustomsGraph.deepLinks.map { navDeepLink<Scales>("${it}scales") }

                public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                    text = { Text(label) },
                    icon = { Icon(Icons.Outlined.MonitorWeight, label) },
                    selectedIcon = { Icon(Icons.Filled.MonitorWeight, label) },
                    route = Scales(),
                )
            }
        }

        @Serializable
        public data object Document : Destination {
            public val deepLinks: List<NavDeepLink>
                get() = Destination.CustomsGraph.deepLinks.map { navDeepLink<Document>("${it}document") }

            public fun navigationItem(label: String): NavigationItem<Destination> = NavigationItem(
                text = { Text(label) },
                icon = { Icon(Icons.Outlined.Book, label) },
                selectedIcon = { Icon(Icons.Filled.Book, label) },
                route = Document,
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
            customsCMSCameraLabel: String,
            customsCMSXrayLabel: String,
            customsCMSScalesLabel: String,
            customsCDoxDocumentLabel: String,
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
            CustomsGraph.Camera.navigationItem(customsCMSCameraLabel),
            CustomsGraph.Xray.navigationItem(customsCMSXrayLabel),
            CustomsGraph.Scales.navigationItem(customsCMSScalesLabel),
            CustomsGraph.Document.navigationItem(customsCDoxDocumentLabel),
        )
    }
}
