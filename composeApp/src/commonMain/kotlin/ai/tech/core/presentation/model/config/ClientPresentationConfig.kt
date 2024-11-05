package ai.tech.core.presentation.model.config

import ai.tech.core.misc.type.serializer.colorscheme.ColorSchemeJson
import ai.tech.core.misc.type.serializer.shapes.ShapesJson
import ai.tech.core.misc.type.serializer.typography.TypographyJson
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import kotlinx.serialization.Serializable

@Serializable
public data class ClientPresentationConfigImpl(
    override val colorScheme: ColorSchemeJson? = null,
    override val shapes: ShapesJson = Shapes(),
    override val typography: TypographyJson = Typography(),
    override val routeBase: String? = null,
    override val startDestination: String,
    override val signInRedirectDestination: String = startDestination,
    override val signOutRedirectDestination: String = startDestination,
    override val destination: SharedDestinationsConfig
) : ClientPresentationConfig
