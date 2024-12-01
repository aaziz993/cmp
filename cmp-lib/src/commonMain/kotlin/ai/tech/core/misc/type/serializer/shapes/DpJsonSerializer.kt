package ai.tech.core.misc.type.serializer.shapes

import ai.tech.core.misc.type.serialization.serializer.primitive.PrimitiveFloatSerializer
import androidx.compose.ui.unit.Dp
import kotlinx.serialization.Serializable

public object DpJsonSerializer :
    PrimitiveFloatSerializer<Dp>(
        Dp::class,
        Dp::value,
        ::Dp,
    )

public typealias DpSerial = @Serializable(with = DpJsonSerializer::class) Dp

