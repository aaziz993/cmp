package ai.tech.core.misc.auth.client.oauth

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.oauth.model.AuthenticationFailedCause
import ai.tech.core.misc.auth.client.oauth.model.OAuth1aException
import ai.tech.core.misc.auth.client.oauth.model.OAuthAccessTokenResponse
import ai.tech.core.misc.cryptography.hmacSha1KeyDecodeBlocking
import ai.tech.core.misc.cryptography.hmacSignatureBlocking
import ai.tech.core.misc.type.single.nowEpochMillis
import dev.whyoleg.cryptography.algorithms.symmetric.HMAC
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.IOException

public class OAuth1aImplicitProvider(
    name: String?,
    httpClient: HttpClient,
    public val requestTokenUrl: String,
    public val authorizeUrl: String,
    public val accessTokenUrl: String,
    public val consumerKey: String,
    public val consumerSecret: String,
    public val extraAuthParameters: List<Pair<String, String>> = emptyList(),
    public val extraTokenParameters: List<Pair<String, String>> = emptyList(),
    public val accessTokenInterceptor: HttpRequestBuilder.() -> Unit = {},
    public val nonce: String = generateNonce(),
    callbackRedirectUrl: String,
    keyValue: AbstractKeyValue,
    onRedirectAuthenticate: suspend (url: Url) -> Unit
) : AbstractOAuthProvider<OAuthAccessTokenResponse.OAuth1a>(
    name,
    httpClient,
    callbackRedirectUrl,
    keyValue,
    onRedirectAuthenticate,
) {

    override suspend fun getRedirectUrl(): Url {
        val (token, _) = simpleOAuth1aStep1(
            httpClient,
            "$consumerSecret&",
            requestTokenUrl,
            callbackRedirectUrl,
            consumerKey,
            nonce,
            extraAuthParameters,
        )

        return Url(
            authorizeUrl.appendUrlParameters(
                "${HttpAuthHeader.Parameters.OAuthToken}=${token.encodeURLParameter()}",
            ),
        )
    }

    override suspend fun callback(parameters: Parameters): AuthenticationFailedCause? {
        val token = parameters[HttpAuthHeader.Parameters.OAuthToken]
        val verifier = parameters[HttpAuthHeader.Parameters.OAuthVerifier]
        return when {
            token != null && verifier != null -> try {
                resume(
                    requestOAuth1aAccessToken(
                        httpClient,
                        "$consumerSecret&", // TODO??
                        accessTokenUrl,
                        consumerKey,
                        token,
                        verifier,
                        generateNonce(),
                        extraTokenParameters,
                        accessTokenInterceptor,
                    ),
                )

                null
            }
            catch (_: OAuth1aException.MissingTokenException) {
                AuthenticationFailedCause.InvalidCredentials
            }
            catch (_: Throwable) {
                AuthenticationFailedCause.Error("OAuth1a failed to get OAuth1 access token")
                null
            }

            else -> AuthenticationFailedCause.NoCredentials
        }
    }
}

private suspend fun simpleOAuth1aStep1(
    client: HttpClient,
    secretKey: String,
    baseUrl: String,
    callback: String,
    consumerKey: String,
    nonce: String = generateNonce(),
    extraParameters: List<Pair<String, String>> = emptyList()
): Pair<String, String> {
    val authHeader = createObtainRequestTokenHeaderInternal(
        callback = callback,
        consumerKey = consumerKey,
        nonce = nonce,
    ).signInternal(HttpMethod.Post, baseUrl, secretKey, extraParameters)

    val url = baseUrl.appendUrlParameters(extraParameters.formUrlEncode())

    val response = client.post(url) {
        header(HttpHeaders.Authorization, authHeader.render(HeaderValueEncoding.URI_ENCODE))
        header(HttpHeaders.Accept, ContentType.Any.toString())
    }

    val body = response.bodyAsText()
    try {
        if (response.status != HttpStatusCode.OK) {
            throw IOException("Bad response: $response")
        }

        val parameters = body.parseUrlEncodedParameters()
        require(parameters[HttpAuthHeader.Parameters.OAuthCallbackConfirmed] == "true") {
            "Response parameter oauth_callback_confirmed should be true"
        }

        return Pair(
            parameters[HttpAuthHeader.Parameters.OAuthToken]!!,
            parameters[HttpAuthHeader.Parameters.OAuthTokenSecret]!!,
        )
    }
    catch (cause: Throwable) {
        throw IOException("Failed to acquire request token due to $body", cause)
    }
}

private suspend fun requestOAuth1aAccessToken(
    client: HttpClient,
    secretKey: String,
    baseUrl: String,
    consumerKey: String,
    token: String,
    verifier: String,
    nonce: String = generateNonce(),
    extraParameters: List<Pair<String, String>> = emptyList(),
    accessTokenInterceptor: (HttpRequestBuilder.() -> Unit)?
): OAuthAccessTokenResponse.OAuth1a {
    val params = listOf(HttpAuthHeader.Parameters.OAuthVerifier to verifier) + extraParameters.toList()
    val authHeader = createUpgradeRequestTokenHeaderInternal(consumerKey, token, nonce)
        .signInternal(HttpMethod.Post, baseUrl, secretKey, params)

    // some of really existing OAuth servers don't support other accept header values so keep it
    val body = client.post(baseUrl) {
        header(HttpHeaders.Authorization, authHeader.render(HeaderValueEncoding.URI_ENCODE))
        header(HttpHeaders.Accept, "*/*")
        // some of really existing OAuth servers don't support other accept header values so keep it

        setBody(
            ChannelWriterContent(
                { writeStringUtf8(buildString(params::formUrlEncodeTo)) },
                ContentType.Application.FormUrlEncoded,
            ),
        )

        accessTokenInterceptor?.invoke(this)
    }.body<String>()

    try {
        val parameters = body.parseUrlEncodedParameters()
        return OAuthAccessTokenResponse.OAuth1a(
            parameters[HttpAuthHeader.Parameters.OAuthToken] ?: throw OAuth1aException.MissingTokenException(),
            parameters[HttpAuthHeader.Parameters.OAuthTokenSecret]
                ?: throw OAuth1aException.MissingTokenException(),
            parameters,
        )
    }
    catch (cause: OAuth1aException) {
        throw cause
    }
    catch (cause: Throwable) {
        throw IOException("Failed to acquire request token due to $body", cause)
    }
}

private fun createObtainRequestTokenHeaderInternal(
    callback: String,
    consumerKey: String,
    nonce: String,
    timestamp: Long = nowEpochMillis / 1000
): HttpAuthHeader.Parameterized = HttpAuthHeader.Parameterized(
    authScheme = AuthScheme.OAuth,
    parameters = mapOf(
        HttpAuthHeader.Parameters.OAuthCallback to callback,
        HttpAuthHeader.Parameters.OAuthConsumerKey to consumerKey,
        HttpAuthHeader.Parameters.OAuthNonce to nonce,
        HttpAuthHeader.Parameters.OAuthSignatureMethod to "HMAC-SHA1",
        HttpAuthHeader.Parameters.OAuthTimestamp to timestamp.toString(),
        HttpAuthHeader.Parameters.OAuthVersion to "1.0",
    ),
)

/**
 * Creates an HTTP auth header for OAuth1a upgrade token request.
 */
private fun createUpgradeRequestTokenHeaderInternal(
    consumerKey: String,
    token: String,
    nonce: String,
    timestamp: Long = nowEpochMillis / 1000
): HttpAuthHeader.Parameterized = HttpAuthHeader.Parameterized(
    authScheme = AuthScheme.OAuth,
    parameters = mapOf(
        HttpAuthHeader.Parameters.OAuthConsumerKey to consumerKey,
        HttpAuthHeader.Parameters.OAuthToken to token,
        HttpAuthHeader.Parameters.OAuthNonce to nonce,
        HttpAuthHeader.Parameters.OAuthSignatureMethod to "HMAC-SHA1",
        HttpAuthHeader.Parameters.OAuthTimestamp to timestamp.toString(),
        HttpAuthHeader.Parameters.OAuthVersion to "1.0",
    ),
)

/**
 * Signs an HTTP auth header.
 */
private fun HttpAuthHeader.Parameterized.signInternal(
    method: HttpMethod,
    baseUrl: String,
    key: String,
    parameters: List<Pair<String, String>>
): HttpAuthHeader.Parameterized = withParameter(
    HttpAuthHeader.Parameters.OAuthSignature,
    signatureBaseStringInternal(this, method, baseUrl, parameters.toHeaderParamsList()).hmacSha1(key),
)

/**
 * Builds an OAuth1a signature base string as per RFC.
 */
internal fun signatureBaseStringInternal(
    header: HttpAuthHeader.Parameterized,
    method: HttpMethod,
    baseUrl: String,
    parameters: List<HeaderValueParam>
): String = listOf(
    method.value.toUpperCasePreservingASCIIRules(),
    baseUrl,
    parametersString(header.parameters + parameters),
).joinToString("&") { it.encodeURLParameter() }

private fun String.hmacSha1(key: String): String {
    val decodedKey = key.encodeToByteArray().hmacSha1KeyDecodeBlocking(HMAC.Key.Format.RAW)

    return toByteArray().hmacSignatureBlocking(decodedKey).encodeBase64()
}

private fun parametersString(parameters: List<HeaderValueParam>): String =
    parameters.map { it.name.encodeURLParameter() to it.value.encodeURLParameter() }
        .sortedWith(compareBy<Pair<String, String>> { it.first }.then(compareBy { it.second }))
        .joinToString("&") { "${it.first}=${it.second}" }

internal fun String.appendUrlParameters(parameters: String) =
    when {
        parameters.isEmpty() -> ""
        this.endsWith("?") -> ""
        "?" in this -> "&"
        else -> "?"
    }.let { separator -> "$this$separator$parameters" }
