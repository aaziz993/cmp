package ai.tech.core.misc.type.serializer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor

@OptIn(ExperimentalMaterial3Api::class)
public class TopAppBarColorsJsonSerializer :
    PrimitiveSerializer<TopAppBarColors>(TopAppBarColors::class, { null as TopAppBarColors }, { "" }) {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("TopAppBarDefaults", PrimitiveKind.STRING)
}

@OptIn(ExperimentalMaterial3Api::class)
public typealias TopAppBarColorsJson = @Serializable(with = TopAppBarColorsJsonSerializer::class) TopAppBarColors
