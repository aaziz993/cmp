package ai.tech.core.misc.consul.model.option

import ai.tech.core.misc.type.encodeAnyToString
import ai.tech.core.misc.type.encodeToAny
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
public abstract class ParamAdder {

    @Transient
    private val json = Json {
        explicitNulls = false
    }

    @Suppress("UNCHECKED_CAST")
    @Transient
    public open val query: Map<String, String> = (json.encodeToAny(this) as Map<String, Any>).map { (k, v) ->
        k to json.encodeAnyToString(v)
    }.toMap()

    public open val queryParameters: List<String> = emptyList()

    public open val headers: Map<String, String> = emptyMap()
}
