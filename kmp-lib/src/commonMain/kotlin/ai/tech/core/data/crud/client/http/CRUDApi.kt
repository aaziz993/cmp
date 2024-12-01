package ai.tech.core.data.crud.client.http

import ai.tech.core.data.crud.client.http.model.HttpOperation
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.*
import io.ktor.client.statement.*

internal interface CRUDApi {

    @Headers("Content-Type: application/stream+json")
    @POST("transaction")
    suspend fun transaction(@ReqBuilder ext: suspend HttpRequestBuilder.() -> Unit)

    @Headers("Content-Type: application/json")
    @PUT("insert")
    suspend fun insert(@Body operation: HttpOperation.Insert<*>)

    @Headers("Content-Type: application/json")
    @PUT("insertAndReturn")
    suspend fun insertAndReturn(@Body operation: HttpOperation.InsertAndReturn<*>): HttpStatement

    @Headers("Content-Type: application/json")
    @POST("update")
    suspend fun update(@Body operation: HttpOperation.Update<*>): List<Boolean>

    @Headers("Content-Type: application/json")
    @POST("updateUntyped")
    suspend fun update(@Body operation: HttpOperation.UpdateUntyped): Long

    @Headers("Content-Type: application/json")
    @PUT("upsert")
    suspend fun upsert(@Body operation: HttpOperation.Upsert<*>): HttpStatement

    @Headers("Content-Type: application/json")
    @POST("find")
    suspend fun find(@Body operation: HttpOperation.Find): HttpStatement

    @Headers("Content-Type: application/json")
    @POST("delete")
    suspend fun delete(@Body operation: HttpOperation.Delete): Long

    @Headers("Content-Type: application/json")
    @POST("aggregate")
    suspend fun aggregate(@Body operation: HttpOperation.Aggregate): HttpStatement
}
