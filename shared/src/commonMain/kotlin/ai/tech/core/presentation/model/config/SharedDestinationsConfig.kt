package ai.tech.core.presentation.model.config

import ai.tech.auth.forgotpassword.model.config.ForgotPasswordScreenConfig
import ai.tech.auth.login.model.LoginScreenConfig
import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.model.config.presentation.DestinationsConfig
import ai.tech.home.about.model.config.AboutScreenConfig
import customs.cms.model.CMSScreenConfig
import ai.tech.home.main.model.config.MainScreenConfig
import ai.tech.home.map.model.config.MapScreenConfig
import ai.tech.home.settings.model.config.SettingsScreenConfig
import ai.tech.wallet.balance.model.config.BalanceScreenConfig
import ai.tech.wallet.crypto.model.config.CryptoScreenConfig
import ai.tech.wallet.stock.model.config.StockScreenConfig
import kotlinx.serialization.Serializable
import profile.model.ProfileScreenConfig

@Serializable
public data class SharedDestinationsConfig(
    val main: MainScreenConfig = MainScreenConfig("main"),
    val map: MapScreenConfig,
    val settings: SettingsScreenConfig = SettingsScreenConfig("settings"),
    val about: AboutScreenConfig = AboutScreenConfig("about"),
    val login: LoginScreenConfig = LoginScreenConfig("login"),
    val forgotPassword: ForgotPasswordScreenConfig = ForgotPasswordScreenConfig("forgotpassword"),
    val profile: ProfileScreenConfig = ProfileScreenConfig("profile", AuthResource()),
    val balance: BalanceScreenConfig = BalanceScreenConfig("balance", AuthResource()),
    val crypto: CryptoScreenConfig = CryptoScreenConfig("crypto", AuthResource()),
    val stock: StockScreenConfig = StockScreenConfig("stock", AuthResource()),
    val camera: CMSScreenConfig,
    val xray: CMSScreenConfig,
    val scales: CMSScreenConfig,
) : DestinationsConfig
