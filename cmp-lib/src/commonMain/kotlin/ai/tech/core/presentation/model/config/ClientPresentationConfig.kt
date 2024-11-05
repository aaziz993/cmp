package ai.tech.core.presentation.model.config

import ai.tech.core.misc.model.config.presentation.PresentationConfig
import ai.tech.core.misc.type.serializer.colorscheme.ColorSchemeJson
import ai.tech.core.misc.type.serializer.shapes.ShapesJson
import ai.tech.core.misc.type.serializer.typography.TypographyJson

public interface ClientPresentationConfig : PresentationConfig {
    public val colorScheme: ColorSchemeJson?
    public val shapes: ShapesJson
    public val typography: TypographyJson
}
