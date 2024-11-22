package ai.tech.core.presentation.model.config

import ai.tech.about.model.config.AboutScreenConfig
import ai.tech.auth.forgotpassword.model.config.ForgotPasswordScreenConfig
import ai.tech.auth.login.model.LoginScreenConfig
import ai.tech.auth.profile.model.config.ProfileScreenConfig
import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.main.model.config.MainScreenConfig
import ai.tech.map.model.config.MapScreenConfig
import ai.tech.settings.model.config.SettingsScreenConfig
import ai.tech.wallet.balance.model.config.BalanceScreenConfig
import ai.tech.wallet.crypto.model.config.CryptoScreenConfig
import ai.tech.wallet.stock.model.config.StockScreenConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SharedDestinationConfig(
    val main: MainScreenConfig = MainScreenConfig(),
    val map: MapScreenConfig,
    val settings: SettingsScreenConfig = SettingsScreenConfig(),
    val about: AboutScreenConfig = AboutScreenConfig(),
    val login: LoginScreenConfig = LoginScreenConfig(),
    val forgotPassword: ForgotPasswordScreenConfig = ForgotPasswordScreenConfig(),
    val profile: ProfileScreenConfig = ProfileScreenConfig(auth =  AuthResource()),
    val balance: BalanceScreenConfig = BalanceScreenConfig(),
    val crypto: CryptoScreenConfig = CryptoScreenConfig(),
    val stock: StockScreenConfig = StockScreenConfig(),
) : DestinationConfig
