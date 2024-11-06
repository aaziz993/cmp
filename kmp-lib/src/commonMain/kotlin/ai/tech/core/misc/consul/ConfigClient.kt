package ai.tech.core.misc.consul

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

public class ConfigClient internal constructor(private val client: HttpClient) {

    public suspend fun apply(
        kind: String,
        name: String,
        key: String,
        value: String,
        dc: String? = null,
        cas: Int? = null,
        ns: String? = null
    ) {
        client.put(PATH) {
            parameter("dc", dc)
            parameter("cas", cas)
            parameter("ns", ns)
            setBody(mapOf("Kind" to kind, "Name" to name, key to value))
        }
    }

    public suspend fun read(
        kind: String,
        name: String,
        dc: String? = null,
        ns: String? = null
    ): JsonObject = client.get("$PATH$kind/$name") {
        parameter("dc", dc)
        parameter("ns", ns)
    }.body()

    public suspend fun list(
        kind: String,
        dc: String? = null,
        ns: String? = null
    ): JsonArray = client.get("$PATH$kind") {
        parameter("dc", dc)
        parameter("ns", ns)
    }.body()

    public suspend fun delete(
        kind: String,
        name: String,
        dc: String? = null,
        ns: String? = null
    ) {
        client.delete("$PATH$kind/$name") {
            parameter("dc", dc)
            parameter("ns", ns)
        }
    }

    public companion object {

        public const val PATH: String = "/config/"
    }
}
