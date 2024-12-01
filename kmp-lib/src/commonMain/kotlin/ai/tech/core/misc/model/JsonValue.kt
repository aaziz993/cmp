package ai.tech.core.misc.model

import ai.tech.core.misc.type.serialization.decodeAnyFromString
import ai.tech.core.misc.type.serialization.encodeAnyToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty

public class JsonValue(private var value: Any) {

    public operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): String = Json.Default.encodeAnyToString(value)

    public operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: String
    ) {
        this.value = Json.Default.decodeAnyFromString(value)!!
    }
}
