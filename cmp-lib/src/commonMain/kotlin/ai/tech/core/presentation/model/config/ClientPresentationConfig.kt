package ai.tech.core.presentation.model.config

import ai.tech.core.misc.type.serialization.serializer.colorscheme.ColorSchemeJson
import ai.tech.core.misc.type.serialization.serializer.shapes.ShapesJson
import ai.tech.core.misc.type.serialization.serializer.typography.TypographyJson

public interface ClientPresentationConfig : SharedPresentationConfig {
    public val colorScheme: ColorSchemeJson?
    public val shapes: ShapesJson
    public val typography: TypographyJson
}
