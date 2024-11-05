package ai.tech.core.presentation.model.config

import auth.model.AuthScreenConfig
import core.auth.model.AuthResource
import customs.cms.model.CMSScreenConfig
import home.model.HomeScreenConfig
import kotlinx.serialization.Serializable
import map.model.MapScreenConfig
import profile.model.ProfileScreenConfig
import settings.SettingsScreenConfig

@Serializable
public data class SharedDestinationsConfig(
    val home: HomeScreenConfig = HomeScreenConfig("home"),
    val camera: CMSScreenConfig,
    val xray: CMSScreenConfig,
    val scales: CMSScreenConfig,
    val map: MapScreenConfig,
    val auth: AuthScreenConfig,
    val profile: ProfileScreenConfig = ProfileScreenConfig("profile", AuthResource()),
    val settings: SettingsScreenConfig = SettingsScreenConfig("settings")
) : DestinationsConfig
