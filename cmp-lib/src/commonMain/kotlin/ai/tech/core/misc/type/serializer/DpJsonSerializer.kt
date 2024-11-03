package ai.tech.core.misc.type.serializer

import ai.tech.core.misc.type.serializer.primitive.PrimitiveFloatSerializer
import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import androidx.compose.ui.unit.Dp
import kotlinx.serialization.Serializable

public object DpJsonSerializer :
    PrimitiveFloatSerializer<Dp>(
        Dp::class,
        Dp::value,
        ::Dp,
    )

public typealias DpSerial = @Serializable(with = DpJsonSerializer::class) Dp

