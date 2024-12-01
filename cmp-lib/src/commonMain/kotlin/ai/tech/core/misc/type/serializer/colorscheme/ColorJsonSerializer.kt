package ai.tech.core.misc.type.serializer.colorscheme

import ai.tech.core.misc.type.serialization.serializer.primitive.PrimitiveLongSerializer
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

public object ColorJsonSerializer :
    PrimitiveLongSerializer<Color>(
        Color::class,
        { it.value.toLong() },
        { Color(it.toULong()) },
    )

public typealias ColorSerial = @Serializable(with = ColorJsonSerializer::class) Color

