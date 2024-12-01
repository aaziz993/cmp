package ai.tech.core.misc.type.serialization

import ai.tech.core.misc.type.multiple.toDeepMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties

private val commentRegex = Regex("""^\s*[#;].*$""")
private val listKeyRegex = Regex("""(.*?)\[(\d+)\]""")

@OptIn(ExperimentalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun Properties.decodeStringMapFromString(value: String): Map<String, Any> = mutableMapOf<String, Any>().apply {

    value.lineSequence()
        .withIndex()
        .filter { (_, line) -> line.isNotBlank() && !line.matches(commentRegex) } // Ignore blank lines, comments
        .forEach { (index, line) ->
                val kvSplit = line.split("=", limit = 2)

                require(kvSplit.size == 2) {
                    "Invalid key value on line \"$index\"."
                }

                // Handle keys with array-like structure (e.g., somekey[0], somekey[1], etc.)
                listKeyRegex.matchEntire(kvSplit[0])?.let { matchResult ->
                    val (key, index) = matchResult.destructured.let { (key, index) -> key to index.toInt() }
                    val list = getOrPut(key) { mutableListOf<String>() } as MutableList<String>
                    require(index < list.size) {
                        "Invalid list index on line \"$index\"."
                    }
                    list.add(kvSplit[1])
                }
        }
}

@OptIn(ExperimentalSerializationApi::class)
public fun Properties.decodeMapFromString(value: String): Map<String, Any?> = decodeStringMapFromString(value).toDeepMap(".")
