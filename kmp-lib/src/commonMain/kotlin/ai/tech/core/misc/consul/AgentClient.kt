package ai.tech.core.misc.consul

import ai.tech.core.misc.consul.module.Agent
import ai.tech.core.misc.consul.module.Connect
import ai.tech.core.misc.consul.module.FullService
import ai.tech.core.misc.consul.module.HealthCheck
import ai.tech.core.misc.consul.module.Member
import ai.tech.core.misc.consul.module.Service
import ai.tech.core.misc.consul.module.ServiceCheck
import ai.tech.core.misc.consul.module.ServiceProxy
import ai.tech.core.misc.consul.module.ServiceWeights
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlin.time.Duration
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

public class AgentClient internal constructor(private val client: HttpClient) {

    public suspend fun list(client: HttpClient, wan: Boolean? = null, segment: String? = null): List<Member> = client.get("${PATH}members") {
        parameter("wan", wan)
        parameter("segment", segment)
    }.body()

    public suspend fun self(client: HttpClient): Agent = client.get("${PATH}self").body()

    public suspend fun reload(client: HttpClient): JsonObject? = client.put("${PATH}reload").body()

    public suspend fun maintenance(client: HttpClient, enable: Boolean, reason: String? = null) {
        client.put("${PATH}maintenance") {
            parameter("enable", enable)
            parameter("reason", reason)
        }
    }

    public suspend fun metrics(client: HttpClient): JsonObject = client.get("${PATH}metrics").body()

    public suspend fun join(client: HttpClient, address: String, wan: Boolean? = null) {
        client.put("${PATH}join/$address") {
            parameter("wan", wan)
        }
    }

    public suspend fun leave(client: HttpClient) {
        client.put("${PATH}leave")
    }

    public suspend fun forceLeave(client: HttpClient, node: String, prune: Boolean = false) {
        client.put("${PATH}force-leave/$node") {
            if (prune) parameter("prune", prune)
        }
    }

    public suspend fun updateToken(client: HttpClient, token: String) {
        client.put("${PATH}token/acl_token") {
            setBody(mapOf("Token" to token))
        }
    }

    public suspend fun updateAgentToken(client: HttpClient, token: String) {
        client.put("${PATH}token/acl_agent_token") {
            setBody(mapOf("Token" to token))
        }
    }

    public suspend fun updateMasterToken(client: HttpClient, token: String) {
        client.put("${PATH}token/acl_agent_master_token") {
            setBody(mapOf("Token" to token))
        }
    }

    public suspend fun updateReplicationToken(client: HttpClient, token: String) {
        client.put("${PATH}token/acl_replication_token") {
            setBody(mapOf("Token" to token))
        }
    }

    public suspend fun checks(client: HttpClient, filter: String? = null): Map<String, HealthCheck> = client.get("${PATH}checks") {
        parameter("filter", filter)
    }.body()

    public suspend fun registerCheck(
        client: HttpClient,
        name: String,
        args: List<String>,
        id: String? = null,
        interval: String? = null,
        notes: String? = null,
        deregisterCriticalServiceAfter: String? = null,
        aliasNode: String? = null,
        aliasService: String? = null,
        dockerContainerId: String? = null,
        grpc: String? = null,
        grpcUseTLS: Boolean? = null,
        http: String? = null,
        method: String? = null,
        body: String? = null,
        header: Map<String, List<String>>? = null,
        timeout: Duration? = null,
        outputMaxSize: Int? = null,
        tlsSkipVerify: Boolean? = null,
        tcp: String? = null,
        ttl: String? = null,
        serviceId: String? = null,
        status: String? = null,
        successBeforePassing: Int? = null,
        failureBeforeCritical: Int? = null,
    ): Map<String, HealthCheck> = client.put("${PATH}check/register") {
        setBody(
            mapOf(
                "Name" to name,
                "Args" to args,
                "Id" to id,
                "Interval" to interval,
                "Notes" to notes,
                "DeregisterCriticalServiceAfter" to deregisterCriticalServiceAfter,
                "AliasNode" to aliasNode,
                "AliasService" to aliasService,
                "DockerContainerId" to dockerContainerId,
                "GRPC" to grpc,
                "GRPCUseTLS" to grpcUseTLS,
                "HTTP" to http,
                "Method" to method,
                "Body" to body,
                "Header" to header,
                "Timeout" to timeout,
                "OutputMaxSize" to outputMaxSize,
                "TLSSkipVerify" to tlsSkipVerify,
                "TCP" to tcp,
                "TTL" to ttl,
                "ServiceId" to serviceId,
                "Status" to status,
                "SuccessBeforePassing" to successBeforePassing,
                "FailureBeforeCritical" to failureBeforeCritical,
            ).filterValues { it != null },
        )
    }.body()

    public suspend fun deregisterCheck(client: HttpClient, checkId: String) {
        client.put("${PATH}check/deregister/$checkId")
    }

    public suspend fun ttlCheckPass(client: HttpClient, checkId: String, note: String? = null) {
        client.put("${PATH}check/pass/$checkId") {
            setBody(mapOf("note" to note).filterValues { it != null })
        }
    }

    public suspend fun ttlCheckFail(client: HttpClient, checkId: String, note: String? = null) {
        client.put("${PATH}check/fail/$checkId") {
            setBody(mapOf("note" to note).filterValues { it != null })
        }
    }

    public suspend fun ttlCheckUpdate(client: HttpClient, checkId: String, status: String, note: String? = null) {
        client.put("${PATH}check/update/$checkId") {
            setBody(mapOf("status" to status, "note" to note).filterValues { it != null })
        }
    }

    public suspend fun services(client: HttpClient, filter: String? = null): Map<String, Service> =
        client.get("${PATH}services") {
            parameter("filter", filter)
        }.body()

    public suspend fun service(client: HttpClient, id: String): FullService =
        client.get("${PATH}service/$id").body()

    public suspend fun healthByName(client: HttpClient, serviceName: String): Map<String, JsonArray> =
        client.get("${PATH}health/service/name/$serviceName").body()

    public suspend fun healthById(client: HttpClient, serviceId: String): String =
        client.get("${PATH}health/service/id/$serviceId").bodyAsText()

    public suspend fun register(
        client: HttpClient,
        name: String,
        id: String? = null,
        tags: List<String>? = null,
        address: String? = null,
        taggedAddress: Map<String, *>? = null,
        meta: Map<String, String>? = null,
        port: Int? = null,
        kind: String? = null,
        proxy: ServiceProxy? = null,
        connect: Connect? = null,
        check: ServiceCheck? = null,
        enableTagOverride: Boolean? = null,
        weights: ServiceWeights? = null,
        replaceExistingChecks: Boolean? = null
    ) {
        client.put("${PATH}service/register") {
            parameter("replace-existing-checks", replaceExistingChecks)
            setBody(
                mapOf(
                    "Name" to name,
                    "Id" to id,
                    "Tags" to tags,
                    "Address" to address,
                    "TaggedAddress" to taggedAddress,
                    "Meta" to meta,
                    "Port" to port,
                    "Kind" to kind,
                    "Proxy" to proxy,
                    "Connect" to connect,
                    "Check" to check,
                    "EnableTagOverride" to enableTagOverride,
                    "Weights" to weights,
                ).filterValues { it != null },
            )
        }
    }

    public suspend fun deregister(client: HttpClient, serviceId: String) {
        client.put("${PATH}health/deregister/$serviceId")
    }

    public suspend fun maintenance(client: HttpClient, serviceId: String, enable: Boolean, reason: String? = null) {
        client.put("${PATH}health/maintenance/$serviceId") {
            parameter("enable", enable)
            parameter("reason", reason)
        }
    }

    public suspend fun authorizeConnect(
        client: HttpClient,
        target: String,
        clientCertURI: String,
        clientCertSerial: String,
        namespace: String? = null
    ): JsonObject = client.post("${PATH}connect/authorize") {
        setBody(
            mapOf(
                "Target" to target,
                "ClientCertURI" to clientCertURI,
                "ClientCertSerial" to clientCertSerial,
                "Namespace" to namespace,
            ).filterValues { it != null },
        )
    }.body()

    public suspend fun caRoots(client: HttpClient): JsonObject =
        client.get("${PATH}connect/ca/roots").body()

    public suspend fun serviceCert(client: HttpClient, serviceName: String): JsonObject =
        client.get("${PATH}connect/ca/leaf/$serviceName").body()

    public companion object {

        public const val PATH: String = "/agent/"
    }
}
