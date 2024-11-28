package ai.tech.core.data.crud.client.http

import ai.tech.core.data.expression.BooleanVariable
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpStatement

internal interface CRUDApi {

    @Headers("Content-Type: application/json")
    @PUT("insert")
    suspend fun insert(@Body entities: List<*>)

    @Headers("Content-Type: application/json")
    @PUT("insertAndReturn")
    suspend fun insertAndReturn(@Body entities: List<*>): HttpStatement

    @Headers("Content-Type: application/json")
    @POST("updateSafe")
    suspend fun update(@Body entities: List<*>): List<Boolean>

    @Multipart
    @POST("update")
    suspend fun update(@Body map: MultiPartFormDataContent): List<Long>

    @Headers("Content-Type: application/json")
    @PUT("upsert")
    suspend fun upsert(@Body entities: List<*>): HttpStatement

    @Headers("Content-Type: application/json")
    @POST("delete")
    suspend fun delete(@Body predicate: BooleanVariable? = null): Long

    @POST("aggregate")
    suspend fun aggregate(@Body map: MultiPartFormDataContent): HttpStatement

    @POST("find")
    suspend fun find(@Body map: MultiPartFormDataContent): HttpStatement
}
