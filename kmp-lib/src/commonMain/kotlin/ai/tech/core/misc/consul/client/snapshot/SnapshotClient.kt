package ai.tech.core.misc.consul.client.snapshot

import ai.tech.core.data.filesystem.fsPathRead
import ai.tech.core.data.filesystem.fsPathWrite
import ai.tech.core.misc.consul.model.parameter.QueryParameters
import ai.tech.core.misc.type.multiple.asyncIterator
import ai.tech.core.misc.type.multiple.toList
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.statement.bodyAsChannel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

/**
 * HTTP Client for /v1/snapshot/ endpoints.
 *
 * @see [The Consul API Docs](https://www.consul.io/api/snapshot.html) for Snapshots
 */
public class SnapshotClient internal constructor(ktorfit: Ktorfit) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: SnapshotApi = ktorfit.createSnapshotApi()

    /**
     * Requests a new snapshot and save it in a file.
     * Only a subset of the QueryParameters is supported: datacenter, consistencymode, and token.
     * @param destinationFile file in which the snapshot is to be saved.
     * @param queryParameters query options. Only a subset of the QueryParameters is supported: datacenter, consistencymode, and token.
     * @param callback callback called once the operation is over. It the save operation is successful, the X-Consul-Index is send.
     */
    public suspend fun generate(filePath: String, queryParameters: QueryParameters = QueryParameters()): Unit =
        filePath.fsPathWrite(api.generate(queryParameters.query).execute().bodyAsChannel().asyncIterator())

    /**
     * Restores a snapshot stored in a file.
     * @param sourceFile source file where the snapshot is stored.
     * @param queryParameters query options. Only a subset of the QueryParameters is supported: datacenter, token.
     * @param callback callback called once the operation is over.
     */
    public suspend fun restore(filePath: String, queryParameters: QueryParameters = QueryParameters()): Unit =
        api.restore(queryParameters.query, Json.Default.encodeToJsonElement(filePath.fsPathRead().toList().fold(byteArrayOf()) { acc, v -> acc + v }))
}
