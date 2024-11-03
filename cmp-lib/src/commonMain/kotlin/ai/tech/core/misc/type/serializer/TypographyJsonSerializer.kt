package ai.tech.core.misc.type.serializer

import androidx.compose.material3.Typography
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor

public object TypographyJsonSerializer : PrimitiveSerializer<Typography>(Typography::class, { Typography() }, { "" }) {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Typography", PrimitiveKind.STRING)
}

public typealias TypographyJson = @Serializable(with = TypographyJsonSerializer::class) Typography
