package ai.tech.core.misc.type.serializer

import androidx.compose.ui.unit.Dp
import kotlinx.serialization.Serializable

public class DpSerializer :
    PrimitiveSerializer<Dp>(
        Dp::class,
        { Dp(it.toFloat()) },
        { it.value.toString() }
    )


public typealias DpSerial = @Serializable(with = DpSerializer::class) Dp

