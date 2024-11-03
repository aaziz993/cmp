package ai.tech.core.presentation.model.config

import ai.tech.core.misc.type.serializer.ColorSchemeJson
import ai.tech.core.misc.type.serializer.ShapesJson
import ai.tech.core.misc.type.serializer.TypographyJson

public interface ClientPresentationConfig<T : Any> : SharedPresentationConfig {
    public val colorScheme: ColorSchemeJson?
    public val shapes: ShapesJson
    public val typography: TypographyJson
    public val screen: DestinationsConfig
}
