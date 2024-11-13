package ai.tech.core.misc.consul.client.keyvalue

import ai.tech.core.misc.consul.client.keyvalue.model.TxResponse
import ai.tech.core.misc.consul.client.keyvalue.model.Value
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import kotlinx.serialization.json.JsonElement

public interface KVApi {

    @GET("kv/{key}")
    public suspend fun getValue(
        @Path("key") key: String,
        @QueryMap query: Map<String, String> = emptyMap()
    ): List<Value>

    @GET("kv/{key}")
    public suspend fun getKeys(
        @Path("key") key: String,
        @QueryMap query: Map<String, String> = emptyMap()
    ): List<String>

    @PUT("kv/{key}")
    public suspend fun putValue(
        @Path("key") key: String,
        @QueryMap query: Map<String, String> = emptyMap()
    ): Boolean

    @PUT("kv/{key}")
    public suspend fun putValue(
        @Path("key") key: String,
        @Body data: JsonElement,
        @QueryMap query: Map<String, String> = emptyMap()
    ): Boolean

    @DELETE("kv/{key}")
    public suspend fun deleteValues(
        @Path("key") key: String,
        @QueryMap query: Map<String, String> = emptyMap()
    )

    @PUT("txn")
    @Headers("Content-Type: application/json")
    public suspend fun performTransaction(
        @Body body: JsonElement,
        @QueryMap query: Map<String, String> = emptyMap()
    ): TxResponse
}
