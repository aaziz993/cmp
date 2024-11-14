package ai.tech.core.misc.consul.client.snapshot

import com.orbitz.consul.async.Callback
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * HTTP Client for /v1/snapshot/ endpoints.
 *
 * @see [The Consul API Docs](https://www.consul.io/api/snapshot.html) for Snapshots
 */
public class SnapshotClient internal constructor(ktorfit: Ktorfit){
    /**
     * Constructs an instance of this class.
     *
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: SnapshotApi = ktorfit.createSnapshotApi()

    /**
     * Requests a new snapshot and save it in a file.
     * Only a subset of the QueryOptions is supported: datacenter, consistencymode, and token.
     * @param destinationFile file in which the snapshot is to be saved.
     * @param queryOptions query options. Only a subset of the QueryOptions is supported: datacenter, consistencymode, and token.
     * @param callback callback called once the operation is over. It the save operation is successful, the X-Consul-Index is send.
     */
    public suspend fun save(destinationFile: File, queryOptions: QueryOptions, callback: Callback<BigInteger>) {
        http.extractConsulResponse(
            api.generateSnapshot(queryOptions.toQuery()),
            object : ConsulResponseCallback<ResponseBody>() {
                @Override
                public suspend fun onComplete(consulResponse: ConsulResponse<ResponseBody>) {
                    // Note that response.body() and response.body().byteStream() should be closed.
                    // see: https://square.github.io/okhttp/3.x/okhttp/okhttp3/ResponseBody.html
                    try {
                        consulResponse.getResponse().use { responseBody ->
                            responseBody.byteStream().use { inputStream ->
                                Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                                callback.onResponse(consulResponse.getIndex())
                            }
                        }
                    } catch (e: IOException) {
                        callback.onFailure(e)
                    }
                }

                @Override
                public suspend fun onFailure(t: Throwable) {
                    callback.onFailure(t)
                }
            })
    }

    /**
     * Restores a snapshot stored in a file.
     * @param sourceFile source file where the snapshot is stored.
     * @param queryOptions query options. Only a subset of the QueryOptions is supported: datacenter, token.
     * @param callback callback called once the operation is over.
     */
    public suspend fun restore(sourceFile: File, queryOptions: QueryOptions, callback: Callback<Void>) {
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/binary"), sourceFile)
        http.extractBasicResponse(api.restoreSnapshot(queryOptions.toQuery(), requestBody), callback)
    }
}
