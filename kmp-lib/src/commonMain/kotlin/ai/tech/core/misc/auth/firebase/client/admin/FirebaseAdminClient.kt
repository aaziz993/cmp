package ai.tech.core.misc.auth.firebase.client.admin

import ai.tech.core.misc.auth.firebase.client.admin.FirebaseAdminClient.Companion.END_OF_LIST
import ai.tech.core.misc.auth.firebase.client.admin.model.SignRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignUpResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.BatchDeleteRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.CreateAuthUriRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.CreateAuthUriResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.CreateRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.DeleteRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.FederatedUserId
import ai.tech.core.misc.auth.firebase.client.admin.model.UserRecord
import ai.tech.core.misc.auth.firebase.client.admin.model.ListResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.LookupRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.LookupResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.OobRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.ResetPasswordRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.ResetPasswordResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SendOobRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SendOobResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithCustomTokenRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithCustomTokenResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithIdpRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignInWithIdpResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.UserInfo
import ai.tech.core.misc.auth.firebase.client.admin.model.TokenRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.TokenResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.UpdateRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.UpdateResponse
import ai.tech.core.misc.network.http.client.AbstractApiHttpClient
import ai.tech.core.misc.type.multiple.model.AbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import io.ktor.client.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.parameters
import kotlin.String
import kotlinx.serialization.json.JsonObject

public class FirebaseAdminClient(
    httpClient: HttpClient,
    public val address: String,
    public val apiKey: String,
) : AbstractApiHttpClient(httpClient, address) {

    private val api = ktorfit.createFirebaseAdminApi()

    override fun HttpClientConfig<*>.configureHttpClient() {
        defaultRequest {
            parameters {
                append("apiKey", apiKey)
            }
        }
    }

    public suspend fun signInWithCustomToken(token: String): SignInWithCustomTokenResponse =
        api.signInWithCustomToken(SignInWithCustomTokenRequest(token))

    public suspend fun getToken(refreshToken: String): TokenResponse =
        api.getToken(TokenRequest(refreshToken = refreshToken))

    public suspend fun signUp(email: String, password: String): SignUpResponse =
        api.signUp(SignRequest(email, password))

    public suspend fun signInWithPassword(email: String, password: String): SignInResponse =
        api.signInWithPassword(SignRequest(email, password))

    public suspend fun signInAnonymously(): SignUpResponse =
        api.signUp(SignRequest())

    public suspend fun signInWithIdp(
        idToken: String,
        requestUri: String,
        postBody: String,
        returnIdpCredential: Boolean = true
    ): SignInWithIdpResponse =
        api.signInWithIdp(SignInWithIdpRequest(idToken, requestUri, postBody, returnIdpCredential = returnIdpCredential))

    public suspend fun lookup(idToken: String): LookupResponse =
        api.lookup(LookupRequest(idToken))

    public suspend fun lookupById(localId: List<String>): LookupResponse =
        api.lookup(LookupRequest(localId = localId))

    public suspend fun lookupByEmail(email: List<String>): LookupResponse =
        api.lookup(LookupRequest(email = email))

    public suspend fun lookupByPhoneNumber(phoneNumber: List<String>): LookupResponse =
        api.lookup(LookupRequest(phoneNumber = phoneNumber))

    public suspend fun lookupByFederatedUserId(federatedUserId: List<FederatedUserId>): LookupResponse =
        api.lookup(LookupRequest(federatedUserId = federatedUserId))

    public fun batchGet(maxResults: Int = 100): AsyncIterator<List<UserRecord>> =
        FirebaseUsersAsyncIterator { api.batchGet(maxResults, it) }

    public suspend fun create(
        localId: String,
        email: String,
        emailVerified: Boolean = true,
        phoneNumber: String? = null,
        // Password string that is at least 6 characters long.
        password: String,
        displayName: String? = null,
        photoUrl: String? = null,
        disabled: Boolean = false,
    ): UserRecord =
        api.create(
            CreateRequest(
                localId,
                email,
                emailVerified,
                phoneNumber,
                // Password string that is at least 6 characters long.
                password,
                displayName,
                photoUrl,
                disabled,
            ),
        )

    public suspend fun update(
        localId: String? = null,
        oobCode: String? = null,
        email: String? = null,
        passwordHash: String? = null,
        providerUserInfo: UserInfo? = null,
        idToken: String? = null,
        refreshToken: String? = null,
        expiresIn: String? = null,
        phoneNumber: String? = null,
        emailVerified: Boolean? = null,
        displayName: String? = null,
        photoUrl: String? = null,
        disabled: Boolean? = null,
        password: String? = null,
        customClaims: JsonObject? = null,
        deleteProvider: List<String>? = null,
        idSince: Long? = null,
        returnSecureToken: String? = null,
    ): UpdateResponse = api.update(
        UpdateRequest(
            localId,
            oobCode,
            email,
            passwordHash,
            providerUserInfo,
            idToken,
            refreshToken,
            expiresIn,
            phoneNumber,
            emailVerified,
            displayName,
            photoUrl,
            disabled,
            password,
            customClaims,
            deleteProvider,
            idSince,
            returnSecureToken,
        ),
    )

    public suspend fun delete(localId: String): Unit =
        api.batchDelete(DeleteRequest(localId))

    public suspend fun batchDelete(localIds: List<String>, force: Boolean = true): Unit =
        api.batchDelete(BatchDeleteRequest(localIds, force))

    public suspend fun createAuthUri(
        identifier: String,
        continueUri: String,
    ): CreateAuthUriResponse =
        api.createAuthUri(
            CreateAuthUriRequest(
                identifier,
                continueUri,
            ),
        )

    public suspend fun sendOdbCode(
        requestType: OobRequest,
        email: String,
    ): SendOobResponse = api.sendOobCode(SendOobRequest(requestType, email))

    public suspend fun resetPassword(
        oobCode: String,
        newPassword: String? = null
    ): ResetPasswordResponse =
        api.resetPassword(ResetPasswordRequest(oobCode, newPassword))

    public companion object {

        public const val END_OF_LIST: String = "";
    }
}

private class FirebaseUsersAsyncIterator(
    private val getResponse: suspend (pageToken: String?) -> ListResponse
) : AbstractAsyncIterator<List<UserRecord>>() {

    private var pageToken: String? = null

    override suspend fun computeNext() {
        if (pageToken == END_OF_LIST) {
            done()
            return
        }

        setNext(
            getResponse(pageToken).also {
                pageToken = it.nextPageToken
            }.users,
        )
    }
}
