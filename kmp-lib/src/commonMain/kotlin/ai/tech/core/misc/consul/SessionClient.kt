package ai.tech.core.misc.consul

import ai.tech.core.misc.consul.module.Behavior
import ai.tech.core.misc.consul.module.SessionInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

public class SessionClient internal constructor(private val client: HttpClient) {

    public suspend fun create(
        name: String,
        node: String? = null,
        dc: String? = null,
        lockDelay: String? = null,
        checks: List<String>? = null,
        behavior: Behavior = Behavior.RELEASE,
        ttl: String? = null
    ): String = client.put("${PATH}create") {
        setBody(
            mapOf(
                "dc" to dc,
                "LockDelay" to lockDelay,
                "Name" to name,
                "Node" to node,
                "Checks" to checks,
                "Behavior" to behavior.label,
                "TTL" to ttl,
            ).filterValues { it != null },
        )
    }.body<Map<String, String>>()["ID"]!!

    public suspend fun delete(session: String, dc: String? = null): Boolean = client.put("${PATH}destroy/$session") {
        setBody(
            hashMapOf("dc" to dc).filterValues { it != null },
        )
    }.bodyAsText().trim().toBoolean()

    public suspend fun read(session: String, dc: String? = null): List<SessionInfo> = client.get("${PATH}info/$session") {
        parameter("dc", dc)
    }.body()

    public suspend fun listForNode(node: String, dc: String? = null): List<SessionInfo> = client.get("${PATH}node/$node") {
        parameter("dc", dc)
    }.body()

    public suspend fun listActive(dc: String? = null): List<SessionInfo> = client.get("${PATH}list") {
        parameter("dc", dc)
    }.body()

    public suspend fun renew(session: String, dc: String? = null): List<SessionInfo> = client.get("${PATH}session/$session") {
        parameter("dc", dc)
    }.body()

    public companion object {

        public const val PATH: String = "/session/"
    }
}
