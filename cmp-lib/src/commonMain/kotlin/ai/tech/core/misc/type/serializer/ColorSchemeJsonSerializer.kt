package ai.tech.core.misc.type.serializer

import androidx.compose.material3.ColorScheme
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

public object ColorSchemeJsonSerializer : KSerializer<ColorScheme> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorScheme") {
        element("primary", ColorJsonSerializer.descriptor)
        element("onPrimary", ColorJsonSerializer.descriptor)
        element("primaryContainer", ColorJsonSerializer.descriptor)
        element("onPrimaryContainer", ColorJsonSerializer.descriptor)
        element("inversePrimary", ColorJsonSerializer.descriptor)
        element("secondary", ColorJsonSerializer.descriptor)
        element("onSecondary", ColorJsonSerializer.descriptor)
        element("secondaryContainer", ColorJsonSerializer.descriptor)
        element("onSecondaryContainer", ColorJsonSerializer.descriptor)
        element("tertiary", ColorJsonSerializer.descriptor)
        element("onTertiary", ColorJsonSerializer.descriptor)
        element("tertiaryContainer", ColorJsonSerializer.descriptor)
        element("onTertiaryContainer", ColorJsonSerializer.descriptor)
        element("background", ColorJsonSerializer.descriptor)
        element("onBackground", ColorJsonSerializer.descriptor)
        element("surface", ColorJsonSerializer.descriptor)
        element("onSurface", ColorJsonSerializer.descriptor)
        element("surfaceVariant", ColorJsonSerializer.descriptor)
        element("onSurfaceVariant", ColorJsonSerializer.descriptor)
        element("surfaceTint", ColorJsonSerializer.descriptor)
        element("inverseSurface", ColorJsonSerializer.descriptor)
        element("inverseOnSurface", ColorJsonSerializer.descriptor)
        element("error", ColorJsonSerializer.descriptor)
        element("onError", ColorJsonSerializer.descriptor)
        element("errorContainer", ColorJsonSerializer.descriptor)
        element("onErrorContainer", ColorJsonSerializer.descriptor)
        element("outline", ColorJsonSerializer.descriptor)
        element("outlineVariant", ColorJsonSerializer.descriptor)
        element("scrim", ColorJsonSerializer.descriptor)
        element("surfaceBright", ColorJsonSerializer.descriptor)
        element("surfaceDim", ColorJsonSerializer.descriptor)
        element("surfaceContainer", ColorJsonSerializer.descriptor)
        element("surfaceContainerHigh", ColorJsonSerializer.descriptor)
        element("surfaceContainerHighest", ColorJsonSerializer.descriptor)
        element("surfaceContainerLow", ColorJsonSerializer.descriptor)
        element("surfaceContainerLowest", ColorJsonSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: ColorScheme) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, ColorJsonSerializer, value.primary)
            encodeSerializableElement(descriptor, 1, ColorJsonSerializer, value.onPrimary)
            encodeSerializableElement(descriptor, 2, ColorJsonSerializer, value.primaryContainer)
            encodeSerializableElement(descriptor, 3, ColorJsonSerializer, value.onPrimaryContainer)
            encodeSerializableElement(descriptor, 4, ColorJsonSerializer, value.inversePrimary)
            encodeSerializableElement(descriptor, 5, ColorJsonSerializer, value.secondary)
            encodeSerializableElement(descriptor, 6, ColorJsonSerializer, value.onSecondary)
            encodeSerializableElement(descriptor, 7, ColorJsonSerializer, value.secondaryContainer)
            encodeSerializableElement(descriptor, 8, ColorJsonSerializer, value.onSecondaryContainer)
            encodeSerializableElement(descriptor, 9, ColorJsonSerializer, value.tertiary)
            encodeSerializableElement(descriptor, 10, ColorJsonSerializer, value.onTertiary)
            encodeSerializableElement(descriptor, 11, ColorJsonSerializer, value.tertiaryContainer)
            encodeSerializableElement(descriptor, 12, ColorJsonSerializer, value.onTertiaryContainer)
            encodeSerializableElement(descriptor, 13, ColorJsonSerializer, value.background)
            encodeSerializableElement(descriptor, 14, ColorJsonSerializer, value.onBackground)
            encodeSerializableElement(descriptor, 15, ColorJsonSerializer, value.surface)
            encodeSerializableElement(descriptor, 16, ColorJsonSerializer, value.onSurface)
            encodeSerializableElement(descriptor, 17, ColorJsonSerializer, value.surfaceVariant)
            encodeSerializableElement(descriptor, 18, ColorJsonSerializer, value.onSurfaceVariant)
            encodeSerializableElement(descriptor, 19, ColorJsonSerializer, value.surfaceTint)
            encodeSerializableElement(descriptor, 20, ColorJsonSerializer, value.inverseSurface)
            encodeSerializableElement(descriptor, 21, ColorJsonSerializer, value.inverseOnSurface)
            encodeSerializableElement(descriptor, 22, ColorJsonSerializer, value.error)
            encodeSerializableElement(descriptor, 23, ColorJsonSerializer, value.onError)
            encodeSerializableElement(descriptor, 24, ColorJsonSerializer, value.errorContainer)
            encodeSerializableElement(descriptor, 25, ColorJsonSerializer, value.onErrorContainer)
            encodeSerializableElement(descriptor, 26, ColorJsonSerializer, value.outline)
            encodeSerializableElement(descriptor, 27, ColorJsonSerializer, value.outlineVariant)
            encodeSerializableElement(descriptor, 28, ColorJsonSerializer, value.scrim)
            encodeSerializableElement(descriptor, 29, ColorJsonSerializer, value.surfaceBright)
            encodeSerializableElement(descriptor, 30, ColorJsonSerializer, value.surfaceDim)
            encodeSerializableElement(descriptor, 31, ColorJsonSerializer, value.surfaceContainer)
            encodeSerializableElement(descriptor, 32, ColorJsonSerializer, value.surfaceContainerHigh)
            encodeSerializableElement(descriptor, 33, ColorJsonSerializer, value.surfaceContainerHighest)
            encodeSerializableElement(descriptor, 34, ColorJsonSerializer, value.surfaceContainerLow)
            encodeSerializableElement(descriptor, 35, ColorJsonSerializer, value.surfaceContainerLowest)
        }
    }

    override fun deserialize(decoder: Decoder): ColorScheme {
        return decoder.decodeStructure(descriptor) {
            ColorScheme(
                primary = decodeSerializableElement(descriptor, 0, ColorJsonSerializer),
                onPrimary = decodeSerializableElement(descriptor, 1, ColorJsonSerializer),
                primaryContainer = decodeSerializableElement(descriptor, 2, ColorJsonSerializer),
                onPrimaryContainer = decodeSerializableElement(descriptor, 3, ColorJsonSerializer),
                inversePrimary = decodeSerializableElement(descriptor, 4, ColorJsonSerializer),
                secondary = decodeSerializableElement(descriptor, 5, ColorJsonSerializer),
                onSecondary = decodeSerializableElement(descriptor, 6, ColorJsonSerializer),
                secondaryContainer = decodeSerializableElement(descriptor, 7, ColorJsonSerializer),
                onSecondaryContainer = decodeSerializableElement(descriptor, 8, ColorJsonSerializer),
                tertiary = decodeSerializableElement(descriptor, 9, ColorJsonSerializer),
                onTertiary = decodeSerializableElement(descriptor, 10, ColorJsonSerializer),
                tertiaryContainer = decodeSerializableElement(descriptor, 11, ColorJsonSerializer),
                onTertiaryContainer = decodeSerializableElement(descriptor, 12, ColorJsonSerializer),
                background = decodeSerializableElement(descriptor, 13, ColorJsonSerializer),
                onBackground = decodeSerializableElement(descriptor, 14, ColorJsonSerializer),
                surface = decodeSerializableElement(descriptor, 15, ColorJsonSerializer),
                onSurface = decodeSerializableElement(descriptor, 16, ColorJsonSerializer),
                surfaceVariant = decodeSerializableElement(descriptor, 17, ColorJsonSerializer),
                onSurfaceVariant = decodeSerializableElement(descriptor, 18, ColorJsonSerializer),
                surfaceTint = decodeSerializableElement(descriptor, 19, ColorJsonSerializer),
                inverseSurface = decodeSerializableElement(descriptor, 20, ColorJsonSerializer),
                inverseOnSurface = decodeSerializableElement(descriptor, 21, ColorJsonSerializer),
                error = decodeSerializableElement(descriptor, 22, ColorJsonSerializer),
                onError = decodeSerializableElement(descriptor, 23, ColorJsonSerializer),
                errorContainer = decodeSerializableElement(descriptor, 24, ColorJsonSerializer),
                onErrorContainer = decodeSerializableElement(descriptor, 25, ColorJsonSerializer),
                outline = decodeSerializableElement(descriptor, 26, ColorJsonSerializer),
                outlineVariant = decodeSerializableElement(descriptor, 27, ColorJsonSerializer),
                scrim = decodeSerializableElement(descriptor, 28, ColorJsonSerializer),
                surfaceBright = decodeSerializableElement(descriptor, 29, ColorJsonSerializer),
                surfaceDim = decodeSerializableElement(descriptor, 30, ColorJsonSerializer),
                surfaceContainer = decodeSerializableElement(descriptor, 31, ColorJsonSerializer),
                surfaceContainerHigh = decodeSerializableElement(descriptor, 32, ColorJsonSerializer),
                surfaceContainerHighest = decodeSerializableElement(descriptor, 33, ColorJsonSerializer),
                surfaceContainerLow = decodeSerializableElement(descriptor, 34, ColorJsonSerializer),
                surfaceContainerLowest = decodeSerializableElement(descriptor, 35, ColorJsonSerializer),
            )
        }
    }
}
public typealias ColorSchemeJson = @Serializable(with = ColorSchemeJsonSerializer::class) ColorScheme
