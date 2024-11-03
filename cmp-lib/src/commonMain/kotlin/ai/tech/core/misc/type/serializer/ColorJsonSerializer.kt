package ai.tech.core.misc.type.serializer

import ai.tech.core.misc.type.serializer.primitive.PrimitiveLongSerializer
import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import kotlinx.serialization.Serializable
import androidx.compose.ui.graphics.Color

public object ColorJsonSerializer :
    PrimitiveLongSerializer<Color>(
        Color::class,
        { it.value.toLong() },
        ::Color,
    )

public typealias ColorSerial = @Serializable(with = ColorJsonSerializer::class) Color

