package ai.tech.core.misc.type.serializer

import kotlinx.serialization.Serializable
import androidx.compose.ui.graphics.Color

public object ColorJsonSerializer :
    PrimitiveSerializer<Color>(
        Color::class,
        { Color(it.toULong()) },
        { it.value.toString() },
    )

public typealias ColorSerial = @Serializable(with = ColorJsonSerializer::class) Color

