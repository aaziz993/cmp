package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.model.Country
import ai.tech.core.misc.location.model.Language
import io.github.aaziz993.cmp.lib.cmp_lib.generated.resources.Res
import io.github.aaziz993.cmp.lib.cmp_lib.generated.resources.allDrawableResources
import io.github.aaziz993.cmp.lib.cmp_lib.generated.resources.image_load_error
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
public fun Language.getCountryFlagDrawerResource(): DrawableResource =
    countryAlpha2?.let { Res.allDrawableResources["flag_${it.lowercase()}"] }
        ?: Res.drawable.image_load_error

@OptIn(ExperimentalResourceApi::class)
public fun Country.getFlagDrawerResource(): DrawableResource =
    Res.allDrawableResources["flag_${alpha2.lowercase()}"]
        ?: Res.drawable.image_load_error
