package ai.tech.core.presentation.model.config

import ai.tech.auth.login.model.LoginScreenConfig
import core.auth.model.AuthResource
import customs.cms.model.CMSScreenConfig
import ai.tech.home.main.model.config.MainScreenConfig
import kotlinx.serialization.Serializable
import ai.tech.home.map.model.config.MapScreenConfig
import profile.model.ProfileScreenConfig
import settings.SettingsScreenConfig

@Serializable
public data class SharedDestinationsConfig(
    val home: MainScreenConfig = MainScreenConfig("home"),
    val camera: CMSScreenConfig,
    val xray: CMSScreenConfig,
    val scales: CMSScreenConfig,
    val map: MapScreenConfig,
    val auth: LoginScreenConfig,
    val profile: ProfileScreenConfig = ProfileScreenConfig("profile", AuthResource()),
    val settings: SettingsScreenConfig = SettingsScreenConfig("settings")
) : DestinationsConfig
