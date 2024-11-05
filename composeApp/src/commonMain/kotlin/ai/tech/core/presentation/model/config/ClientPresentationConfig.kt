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
    override val route: String,
    override val signInRedirectRoute: String = route,
    override val signOutRedirectRoute: String = route,
    override val destination: SharedDestinationsConfig
) : ClientPresentationConfig
