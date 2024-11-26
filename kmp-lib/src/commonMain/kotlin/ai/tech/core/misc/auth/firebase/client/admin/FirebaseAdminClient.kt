package ai.tech.core.misc.auth.firebase.client.admin

import ai.tech.core.misc.auth.firebase.client.admin.FirebaseAdminClient.Companion.END_OF_LIST
import ai.tech.core.misc.auth.firebase.client.admin.model.SignRequest
import ai.tech.core.misc.auth.firebase.client.admin.model.SignUpResponse
import ai.tech.core.misc.auth.firebase.client.admin.model.BatchDeleteRequest
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
import kotlin.String
import kotlinx.serialization.json.JsonObject

public class FirebaseAdminClient(
    httpClient: HttpClient,
    public val address: String,
    public val apiKey: String,
) : AbstractApiHttpClient(httpClient, address) {

    private val api = ktorfit.createFirebaseAdminApi()

    public suspend fun signInWithCustomToken(token: String): SignInWithCustomTokenResponse =
        api.signInWithCustomToken(apiKey, SignInWithCustomTokenRequest(token))

    public suspend fun getToken(refreshToken: String): TokenResponse =
        api.getToken(
            apiKey,
            TokenRequest(refreshToken = refreshToken),
        )

    public suspend fun signUp(email: String, password: String): SignUpResponse =
        api.signUp(apiKey, SignRequest(email, password))

    public suspend fun signInWithPassword(email: String, password: String): SignInResponse =
        api.signInWithPassword(apiKey, SignRequest(email, password))

    public suspend fun signInAnonymously(): SignUpResponse =
        api.signUp(apiKey, SignRequest())

    public suspend fun signInWithIdp(
        idToken: String,
        requestUri: String,
        postBody: String,
        returnIdpCredential: Boolean = true
    ): SignInWithIdpResponse =
        api.signInWithIdp(apiKey, SignInWithIdpRequest(idToken, requestUri, postBody, returnIdpCredential = returnIdpCredential))

    public suspend fun lookup(idToken: String): LookupResponse =
        api.lookup(apiKey, LookupRequest(idToken))

    public suspend fun lookupById(localId: List<String>): LookupResponse =
        api.lookup(apiKey, LookupRequest(localId = localId))

    public suspend fun lookupByEmail(email: List<String>): LookupResponse =
        api.lookup(apiKey, LookupRequest(email = email))

    public suspend fun lookupByPhoneNumber(phoneNumber: List<String>): LookupResponse =
        api.lookup(apiKey, LookupRequest(phoneNumber = phoneNumber))

    public suspend fun lookupByFederatedUserId(federatedUserId: List<FederatedUserId>): LookupResponse =
        api.lookup(apiKey, LookupRequest(federatedUserId = federatedUserId))

    public fun batchGet(maxResults: Int = 100): AsyncIterator<List<UserRecord>> =
        FirebaseUsersAsyncIterator { api.batchGet(apiKey, maxResults, it) }

    public suspend fun update(
        localId: String,
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
        apiKey,
        UpdateRequest(
            localId,
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
        api.batchDelete(apiKey, DeleteRequest(localId))

    public suspend fun batchDelete(localIds: List<String>, force: Boolean = true): Unit =
        api.batchDelete(apiKey, BatchDeleteRequest(localIds, force))

    public suspend fun sendOdbCode(
        requestType: OobRequest,
        email: String,
    ): SendOobResponse = api.sendOobCode(apiKey, SendOobRequest(requestType, email))

    public suspend fun resetPassword(
        oobCode: String,
        newPassword: String? = null
    ): ResetPasswordResponse =
        api.resetPassword(apiKey, ResetPasswordRequest(oobCode, newPassword))

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
