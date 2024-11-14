package ai.tech.core.misc.type.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.properties.Properties
import net.mamoe.yamlkt.Yaml

@Suppress("UNCHECKED_CAST")
public fun decoderAnyFromString(format: String): (String) -> Any? = when (format) {
    "json" -> {
        { Json.Default.decodeAnyFromString(it) }
    }

    "yaml" -> {
        { Yaml.Default.decodeAnyFromString(it) }
    }

    else -> throw IllegalArgumentException("Invalid format: $format")
}

@OptIn(ExperimentalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun decoderMapFromString(format: String): (String) -> Map<String?, Any?> = when (format) {
    "json" -> {
        { Json.Default.decodeMapFromString(it) as Map<String?, Any?> }
    }

    "yaml" -> {
        { Yaml.Default.decodeMapFromString(it) }
    }

    "properties" -> {
        { Properties.Default.decodeNestedMapFromString(it) as Map<String?, Any?> }
    }

    else -> throw IllegalArgumentException("Invalid format: $format")
}

@Suppress("UNCHECKED_CAST")
public fun decoderListFromString(format: String): (String) -> List<Any?> = when (format) {
    "json" -> {
        { Json.Default.decodeListFromString(it) }
    }

    "yaml" -> {
        { Yaml.Default.decodeListFromString(it) }
    }

    else -> throw IllegalArgumentException("Invalid format: $format")
}
