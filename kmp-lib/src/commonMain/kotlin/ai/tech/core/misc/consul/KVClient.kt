package ai.tech.core.misc.consul

import ai.tech.core.misc.consul.module.KVMetadata
import com.ionspin.kotlin.bignum.integer.BigInteger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

public class KVClient internal constructor(private val client: HttpClient) {

    public suspend fun read(
        key: String,
        dc: String? = null,
        recurse: Boolean? = null,
        index: BigInteger? = null,
        wait: String? = null,
        timeout: Duration = 15.toDuration(DurationUnit.MINUTES)
    ): List<KVMetadata>? = client.get("$PATH$key") {
        parameter("dc", dc)
        parameter("recurse", recurse)
        parameter("index", index)
        parameter("wait", wait)
        timeout {
            requestTimeoutMillis = timeout.inWholeMilliseconds
        }
    }.body()

    public suspend fun write(
        key: String,
        value: Any,
        dc: String? = null,
        flags: Int? = null,
        cas: Int? = null,
        acquire: String? = null,
        release: String? = null,
    ): Boolean = client.put("$PATH$key") {
        parameter("dc", dc)
        parameter("flags", flags)
        parameter("cas", cas)
        parameter("acquire", acquire)
        parameter("release", release)
        setBody(value)
    }.bodyAsText().trim().toBoolean()

    public suspend fun delete(
        key: String,
        dc: String? = null,
        recurse: Boolean? = null,
        cas: Int? = null,
    ): Boolean =
        client.delete("$PATH$key") {
            parameter("dc", dc)
            setBody(
                mapOf(
                    "dc" to dc,
                    "recurse" to recurse,
                    "cas" to cas,
                ).filterValues { it != null },
            )
        }.bodyAsText().trim().toBoolean()

    public companion object {

        public const val PATH: String = "/kv/"
    }
}
