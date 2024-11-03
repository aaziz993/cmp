package ai.tech.core.misc.type.serializer.shapes

import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor

public object ShapesJsonSerializer : PrimitiveStringSerializer<Shapes>(Shapes::class, { Shapes() }, { "" }) {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Shapes", PrimitiveKind.STRING)
}

public typealias ShapesJson = @Serializable(with = ShapesJsonSerializer::class) Shapes
