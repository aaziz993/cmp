//package ai.tech.core.misc.type.serializer
//
//import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
//import androidx.compose.material3.Typography
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.descriptors.PrimitiveKind
//import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
//import kotlinx.serialization.descriptors.SerialDescriptor
//
//public object TypographyJsonSerializer : PrimitiveStringSerializer<Typography>(Typography::class, { Typography() }, { "" }) {
//    override val descriptor: SerialDescriptor
//        get() = PrimitiveSerialDescriptor("Typography", PrimitiveKind.STRING)
//}
//
//public typealias TypographyJson = @Serializable(with = TypographyJsonSerializer::class) Typography
