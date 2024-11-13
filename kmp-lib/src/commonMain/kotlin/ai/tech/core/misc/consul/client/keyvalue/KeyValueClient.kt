package ai.tech.core.misc.consul.client.keyvalue

import ai.tech.core.misc.consul.client.keyvalue.model.TxResponse
import ai.tech.core.misc.consul.client.keyvalue.model.Value
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import kotlinx.serialization.json.JsonElement

internal interface KeyValueClient {

    @GET("kv/{key}")
    suspend fun getValue(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    ): List<Value>

    @GET("kv/{key}")
    suspend fun getKeys(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    ): List<String>

    @PUT("kv/{key}")
    suspend fun putValue(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    ): Boolean

    @PUT("kv/{key}")
    suspend fun putValue(
        @Path("key") key: String,
        @Body data: JsonElement,
        @QueryMap query: Map<String, String>
    ): Boolean

    @DELETE("kv/{key}")
    suspend fun deleteValues(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    )

    @PUT("txn")
    @Headers("Content-Type: application/json")
    suspend fun performTransaction(
        @Body body: JsonElement,
        @QueryMap query: Map<String, String>
    ): TxResponse
}
