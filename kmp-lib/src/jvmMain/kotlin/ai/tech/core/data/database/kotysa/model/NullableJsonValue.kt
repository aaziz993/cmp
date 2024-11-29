package ai.tech.core.data.database.kotysa.model

import ai.tech.core.misc.type.serializer.decodeAnyFromString
import ai.tech.core.misc.type.serializer.encodeAnyToString
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
