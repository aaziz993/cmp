package ai.tech.core.presentation.component.textfield.model

import ai.tech.core.misc.type.isEnum
import ai.tech.core.misc.type.primeTypeOrNull
import kotlin.reflect.typeOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames

public sealed interface TextField {
    public data class Enum(val values: List<String>) : TextField
    public data object LocalTime : TextField
    public data object LocalDate : TextField
    public data object LocalDateTime : TextField
    public data object Text : TextField

    public companion object{
        @OptIn(ExperimentalSerializationApi::class)
        public operator fun invoke(descriptor: SerialDescriptor): TextField = if (descriptor.isEnum) {
            Enum(descriptor.elementNames.toList())
        }
        else when (descriptor.primeTypeOrNull) {
            typeOf<kotlinx.datetime.LocalTime>() -> TextField.LocalTime
            typeOf<kotlinx.datetime.LocalDate>() -> TextField.LocalDate
            typeOf<kotlinx.datetime.LocalDateTime>() -> TextField.LocalDateTime
            else -> TextField.Text
        }
    }
}
