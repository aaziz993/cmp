package ai.tech.core.misc.type.serializer

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor

public class ColorSchemeJsonSerializer :
    PrimitiveSerializer<ColorScheme>(ColorScheme::class, { lightColorScheme() }, { "" }) {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Shapes", PrimitiveKind.STRING)
}


public typealias ColorSchemeJson = @Serializable(with = ColorSchemeJsonSerializer::class) ColorScheme
