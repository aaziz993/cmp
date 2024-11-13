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

internal interface KeyValueClient {

    @GET("kv/{key}")
    fun getValue(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    ): Call<List<Value>>

    @GET("kv/{key}")
    fun getKeys(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    ): Call<List<String>>

    @PUT("kv/{key}")
    fun putValue(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    ): Call<Boolean>

    @PUT("kv/{key}")
    fun putValue(
        @Path("key") key: String,
        @Body data: RequestBody,
        @QueryMap query: Map<String, String>
    ): Call<Boolean>

    @DELETE("kv/{key}")
    fun deleteValues(
        @Path("key") key: String,
        @QueryMap query: Map<String, String>
    ): Call<Unit>

    @PUT("txn")
    @Headers("Content-Type: application/json")
    fun performTransaction(
        @Body body: RequestBody,
        @QueryMap query: Map<String, String>
    ): Call<TxResponse>
}
