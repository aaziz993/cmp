package ai.tech.core.misc.auth.firebase.client.admin

import ai.tech.core.misc.auth.firebase.client.admin.model.BatchDeleteRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.CreateAuthUriRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.CreateAuthUriResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.CreateRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.DeleteRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.ListResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.LookupRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.LookupResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SendOobResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.ResetPasswordRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.ResetPasswordResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SendOobRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithCustomTokenRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithCustomTokenResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithIdpRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithIdpResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SignRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignUpResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.TokenRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.TokenResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.UpdateRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.UpdateResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.UserRecord
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.Serializable

public interface FirebaseAdminApi {

    @POST("accounts:signInWithCustomToken")
    public suspend fun signInWithCustomToken(@Body request: SignInWithCustomTokenRequest): SignInWithCustomTokenResponse

    @POST("token")
    public suspend fun getToken(@Body request: TokenRequest): TokenResponse

    @POST("accounts:signUp")
    public suspend fun signUp(@Body request: SignRequest): SignUpResponse

    @POST("accounts:signInWithPassword")
    public suspend fun signInWithPassword(@Body request: SignRequest): SignInResponse

    @POST("accounts:signInWithIdp")
    public suspend fun signInWithIdp(@Body request: SignInWithIdpRequest): SignInWithIdpResponse

    @POST("accounts:lookup")
    public suspend fun lookup(@Body request: LookupRequest): LookupResponse

    @GET("accounts:batchGet")
    public suspend fun batchGet(
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String? = null
    ): ListResponse

    @POST("accounts")
    public suspend fun create(@Body request: CreateRequest): UserRecord

    @POST("accounts:update")
    public suspend fun update(@Body request: UpdateRequest): UpdateResponse

    @POST("accounts:delete")
    public suspend fun batchDelete(@Body request: DeleteRequest)

    @POST("accounts:batchDelete")
    public suspend fun batchDelete(@Body request: BatchDeleteRequest)

    @POST("accounts:createAuthUri")
    public suspend fun createAuthUri(@Body request: CreateAuthUriRequest): CreateAuthUriResponse

    @POST("accounts:sendOobCode")
    public suspend fun sendOobCode(@Body request: SendOobRequest): SendOobResponse

    @POST("accounts:resetPassword")
    public suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    public companion object {

        public val reservedClaims: List<String> = listOf(
            "amr", "at_hash", "aud", "auth_time", "azp", "cnf", "c_hash", "exp", "iat",
            "iss", "jti", "nbf", "nonce", "sub", "firebase",
        )
    }
}
