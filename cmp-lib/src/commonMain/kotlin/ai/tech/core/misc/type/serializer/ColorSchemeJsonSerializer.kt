package ai.tech.core.misc.type.serializer

import ai.tech.core.ui.theme.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

private val defaultColors = listOf(
    lightColorScheme.primary,
    lightColorScheme.onPrimary,
    lightColorScheme.primaryContainer,
    lightColorScheme.onPrimaryContainer,
    lightColorScheme.inversePrimary,
    lightColorScheme.secondary,
    lightColorScheme.onSecondary,
    lightColorScheme.secondaryContainer,
    lightColorScheme.onSecondaryContainer,
    lightColorScheme.tertiary,
    lightColorScheme.onTertiary,
    lightColorScheme.tertiaryContainer,
    lightColorScheme.onTertiaryContainer,
    lightColorScheme.background,
    lightColorScheme.onBackground,
    lightColorScheme.surface,
    lightColorScheme.onSurface,
    lightColorScheme.surfaceVariant,
    lightColorScheme.onSurfaceVariant,
    lightColorScheme.surfaceTint,
    lightColorScheme.inverseSurface,
    lightColorScheme.inverseOnSurface,
    lightColorScheme.error,
    lightColorScheme.onError,
    lightColorScheme.errorContainer,
    lightColorScheme.onErrorContainer,
    lightColorScheme.outline,
    lightColorScheme.outlineVariant,
    lightColorScheme.scrim,
    lightColorScheme.surfaceBright,
    lightColorScheme.surfaceDim,
    lightColorScheme.surfaceContainer,
    lightColorScheme.surfaceContainerHigh,
    lightColorScheme.surfaceContainerHighest,
    lightColorScheme.surfaceContainerLow,
    lightColorScheme.surfaceContainerLowest,
)

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

    override fun serialize(encoder: Encoder, value: ColorScheme): Unit = with(value) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, ColorJsonSerializer, primary)
            encodeSerializableElement(descriptor, 1, ColorJsonSerializer, onPrimary)
            encodeSerializableElement(descriptor, 2, ColorJsonSerializer, primaryContainer)
            encodeSerializableElement(descriptor, 3, ColorJsonSerializer, onPrimaryContainer)
            encodeSerializableElement(descriptor, 4, ColorJsonSerializer, inversePrimary)
            encodeSerializableElement(descriptor, 5, ColorJsonSerializer, secondary)
            encodeSerializableElement(descriptor, 6, ColorJsonSerializer, onSecondary)
            encodeSerializableElement(descriptor, 7, ColorJsonSerializer, secondaryContainer)
            encodeSerializableElement(descriptor, 8, ColorJsonSerializer, onSecondaryContainer)
            encodeSerializableElement(descriptor, 9, ColorJsonSerializer, tertiary)
            encodeSerializableElement(descriptor, 10, ColorJsonSerializer, onTertiary)
            encodeSerializableElement(descriptor, 11, ColorJsonSerializer, tertiaryContainer)
            encodeSerializableElement(descriptor, 12, ColorJsonSerializer, onTertiaryContainer)
            encodeSerializableElement(descriptor, 13, ColorJsonSerializer, background)
            encodeSerializableElement(descriptor, 14, ColorJsonSerializer, onBackground)
            encodeSerializableElement(descriptor, 15, ColorJsonSerializer, surface)
            encodeSerializableElement(descriptor, 16, ColorJsonSerializer, onSurface)
            encodeSerializableElement(descriptor, 17, ColorJsonSerializer, surfaceVariant)
            encodeSerializableElement(descriptor, 18, ColorJsonSerializer, onSurfaceVariant)
            encodeSerializableElement(descriptor, 19, ColorJsonSerializer, surfaceTint)
            encodeSerializableElement(descriptor, 20, ColorJsonSerializer, inverseSurface)
            encodeSerializableElement(descriptor, 21, ColorJsonSerializer, inverseOnSurface)
            encodeSerializableElement(descriptor, 22, ColorJsonSerializer, error)
            encodeSerializableElement(descriptor, 23, ColorJsonSerializer, onError)
            encodeSerializableElement(descriptor, 24, ColorJsonSerializer, errorContainer)
            encodeSerializableElement(descriptor, 25, ColorJsonSerializer, onErrorContainer)
            encodeSerializableElement(descriptor, 26, ColorJsonSerializer, outline)
            encodeSerializableElement(descriptor, 27, ColorJsonSerializer, outlineVariant)
            encodeSerializableElement(descriptor, 28, ColorJsonSerializer, scrim)
            encodeSerializableElement(descriptor, 29, ColorJsonSerializer, surfaceBright)
            encodeSerializableElement(descriptor, 30, ColorJsonSerializer, surfaceDim)
            encodeSerializableElement(descriptor, 31, ColorJsonSerializer, surfaceContainer)
            encodeSerializableElement(descriptor, 32, ColorJsonSerializer, surfaceContainerHigh)
            encodeSerializableElement(descriptor, 33, ColorJsonSerializer, surfaceContainerHighest)
            encodeSerializableElement(descriptor, 34, ColorJsonSerializer, surfaceContainerLow)
            encodeSerializableElement(descriptor, 35, ColorJsonSerializer, surfaceContainerLowest)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): ColorScheme {
        return decoder.decodeStructure(descriptor) {
            val colors = defaultColors.toMutableList()

            if (decodeSequentially()) {
                (0 until 36).forEach { index ->
                    decodeNullableSerializableElement(descriptor, index, ColorJsonSerializer)?.let { colors[index] = it }
                }
            }
            else {
                do {
                    val index = decodeElementIndex(descriptor)

                    if (index == CompositeDecoder.DECODE_DONE) {
                        break
                    }

                    decodeNullableSerializableElement(descriptor, index, ColorJsonSerializer)?.let { colors[index] = it }
                } while (true)
            }

            ColorScheme(
                colors[0],
                colors[1],
                colors[2],
                colors[3],
                colors[4],
                colors[5],
                colors[6],
                colors[7],
                colors[8],
                colors[9],
                colors[10],
                colors[11],
                colors[12],
                colors[13],
                colors[14],
                colors[15],
                colors[16],
                colors[17],
                colors[18],
                colors[19],
                colors[20],
                colors[21],
                colors[22],
                colors[23],
                colors[24],
                colors[25],
                colors[26],
                colors[27],
                colors[28],
                colors[29],
                colors[30],
                colors[31],
                colors[32],
                colors[33],
                colors[34],
                colors[35],
            )
        }
    }
}

public typealias ColorSchemeJson = @Serializable(with = ColorSchemeJsonSerializer::class) ColorScheme
