package ai.tech.core.misc.model

import ai.tech.core.misc.type.serialization.decodeAnyFromString
import ai.tech.core.misc.type.serialization.encodeAnyToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty

public class NullableJsonValue(private var value: Any?) {

    public operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): String? = value?.let { Json.Default.encodeAnyToString(it) }

    public operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: String?
    ) {
        this.value = value?.let { Json.Default.decodeAnyFromString(it)!! }
    }
}
