package ai.tech.core.misc.auth.client.oauth

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.CredentialAuthProvider
import ai.tech.core.misc.auth.client.oauth.model.AuthenticationFailedCause
import ai.tech.core.misc.auth.client.oauth.model.OAuth2Exception
import ai.tech.core.misc.auth.client.oauth.model.OAuth2RedirectError
import ai.tech.core.misc.auth.client.oauth.model.OAuth2RequestParameters
import ai.tech.core.misc.auth.client.oauth.model.OAuth2ResponseParameters
import ai.tech.core.misc.auth.client.oauth.model.OAuthAccessTokenResponse
import ai.tech.core.misc.auth.client.oauth.model.OAuthGrantTypes
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

public abstract class OAuth2Implicit(
    name: String?,
    httpClient: HttpClient,
    public val authorizeUrl: String,
    public val accessTokenUrl: String,
    refreshTokenUrl: String,
    public val requestMethod: HttpMethod = HttpMethod.Get,
    clientId: String,
    public val clientSecret: String,
    public val defaultScopes: List<String> = emptyList(),
    public val accessTokenRequiresBasicAuth: Boolean = false,
    public val nonceManager: NonceManager = GenerateOnlyNonceManager,
    public val authorizeUrlInterceptor: URLBuilder.() -> Unit = {},
    public val passParamsInURL: Boolean = false,
    public val extraAuthParameters: List<Pair<String, String>> = emptyList(),
    public val extraTokenParameters: List<Pair<String, String>> = emptyList(),
    public val accessTokenInterceptor: HttpRequestBuilder.() -> Unit = {},
    callbackRedirectUrl: String,
    keyValue: AbstractKeyValue,
    private val onRedirectAuthenticateOAuth2: suspend (url: Url) -> Unit
) : AbstractOAuth2Provider(
    name,
    httpClient,
    clientId,
    refreshTokenUrl,
    callbackRedirectUrl,
    keyValue,
    onRedirectAuthenticateOAuth2,
), CredentialAuthProvider {

    // Implements Resource Owner Password Credentials Grant.
    // Takes UserPasswordCredential and validates it using OAuth2 sequence, provides OAuthAccessTokenResponse.
    // OAuth2 if succeeds.
    override suspend fun signIn(
        username: String,
        password: String,
    ): Unit =
        setToken(
            oauth2RequestAccessToken(
                httpClient,
                HttpMethod.Post,
                usedRedirectUrl = null,
                baseUrl = accessTokenUrl,
                clientId = clientId,
                clientSecret = clientSecret,
                code = null,
                state = null,
                configure = accessTokenInterceptor,
                extraParameters = listOf(
                    OAuth2RequestParameters.UserName to username,
                    OAuth2RequestParameters.Password to password,
                ),
                useBasicAuth = true,
                nonceManager = nonceManager,
                passParamsInURL = passParamsInURL,
                grantType = OAuthGrantTypes.Password,
            ),
        )

    override suspend fun getRedirectUrl(): Url = redirectAuthenticateOAuth2(
        authorizeUrl,
        callbackRedirectUrl,
        clientId,
        nonceManager.newNonce(),
        defaultScopes,
        extraAuthParameters,
        authorizeUrlInterceptor,
    )

    override suspend fun callback(parameters: Parameters): AuthenticationFailedCause? {
        val code = parameters[OAuth2RequestParameters.Code]
        val state = parameters[OAuth2RequestParameters.State]
        val error = parameters[OAuth2RequestParameters.Error]
        val errorDescription = parameters[OAuth2RequestParameters.ErrorDescription]

        return when {
            error != null -> OAuth2RedirectError(error, errorDescription)
            code != null && state != null -> try {
                resume(
                    oauth2RequestAccessToken(
                        httpClient,
                        requestMethod,
                        callbackRedirectUrl,
                        accessTokenUrl,
                        clientId,
                        clientSecret,
                        state,
                        code,
                        extraTokenParameters,
                        accessTokenInterceptor,
                        accessTokenRequiresBasicAuth,
                        nonceManager,
                        passParamsInURL,
                    ),
                )

                null
            }
            catch (_: OAuth2Exception.InvalidGrant) {
                AuthenticationFailedCause.InvalidCredentials
            }
            catch (cause: Throwable) {
                AuthenticationFailedCause.Error("Failed to request OAuth2 access token due to $cause")
                null
            }

            else -> AuthenticationFailedCause.NoCredentials
        }
    }
}

private fun redirectAuthenticateOAuth2(
    authenticateUrl: String,
    callbackRedirectUrl: String,
    clientId: String,
    state: String,
    scopes: List<String>,
    parameters: List<Pair<String, String>>,
    interceptor: URLBuilder.() -> Unit
): Url {
    val url = URLBuilder()
    url.takeFrom(authenticateUrl)
    url.parameters.apply {
        append(OAuth2RequestParameters.ClientId, clientId)
        append(OAuth2RequestParameters.RedirectUri, callbackRedirectUrl)
        if (scopes.isNotEmpty()) {
            append(OAuth2RequestParameters.Scope, scopes.joinToString(" "))
        }
        append(OAuth2RequestParameters.State, state)
        append(OAuth2RequestParameters.ResponseType, "code")
        parameters.forEach { (k, v) -> append(k, v) }
    }
    interceptor(url)
    return url.build()
}

private suspend fun oauth2RequestAccessToken(
    client: HttpClient,
    method: HttpMethod,
    usedRedirectUrl: String?,
    baseUrl: String,
    clientId: String,
    clientSecret: String,
    state: String?,
    code: String?,
    extraParameters: List<Pair<String, String>> = emptyList(),
    configure: HttpRequestBuilder.() -> Unit = {},
    useBasicAuth: Boolean = false,
    nonceManager: NonceManager,
    passParamsInURL: Boolean = false,
    grantType: String = OAuthGrantTypes.AuthorizationCode
): OAuthAccessTokenResponse.OAuth2 {
    if (!nonceManager.verifyNonce(state.orEmpty())) {
        throw OAuth2Exception.InvalidNonce()
    }

    val request = HttpRequestBuilder()
    request.url.takeFrom(baseUrl)

    val urlParameters = ParametersBuilder().apply {
        append(OAuth2RequestParameters.ClientId, clientId)
        append(OAuth2RequestParameters.ClientSecret, clientSecret)
        append(OAuth2RequestParameters.GrantType, grantType)
        if (state != null) {
            append(OAuth2RequestParameters.State, state)
        }
        if (code != null) {
            append(OAuth2RequestParameters.Code, code)
        }
        if (usedRedirectUrl != null) {
            append(OAuth2RequestParameters.RedirectUri, usedRedirectUrl)
        }
        extraParameters.forEach { (k, v) -> append(k, v) }
    }.build()

    when (method) {
        HttpMethod.Get -> request.url.parameters.appendAll(urlParameters)
        HttpMethod.Post -> {
            if (passParamsInURL) {
                request.url.parameters.appendAll(urlParameters)
            }
            else {
                request.setBody(
                    TextContent(
                        urlParameters.formUrlEncode(),
                        ContentType.Application.FormUrlEncoded,
                    ),
                )
            }
        }

        else -> throw UnsupportedOperationException("Method $method is not supported. Use GET or POST")
    }

    request.apply {
        this.method = method
        header(
            HttpHeaders.Accept,
            listOf(ContentType.Application.FormUrlEncoded, ContentType.Application.Json).joinToString(","),
        )
        if (useBasicAuth) {
            header(
                HttpHeaders.Authorization,
                HttpAuthHeader.Single(
                    AuthScheme.Basic,
                    "$clientId:$clientSecret".toByteArray(Charsets.ISO_8859_1).encodeBase64(),
                ).render(),
            )
        }

        configure()
    }

    val response = client.request(request)

    val body = response.bodyAsText()

    val (contentType, content) = try {
        if (response.status == HttpStatusCode.NotFound) {
            throw IOException("Access token query failed with http status 404 for the page $baseUrl")
        }
        val contentType = response.headers[HttpHeaders.ContentType]?.let { ContentType.parse(it) }
            ?: ContentType.Any

        Pair(contentType, body)
    }
    catch (ioe: IOException) {
        throw ioe
    }
    catch (cause: Throwable) {
        throw IOException("Failed to acquire request token due to wrong content: $body", cause)
    }

    val contentDecodeResult = Result.runCatching { decodeContent(content, contentType) }
    val errorCode = contentDecodeResult.map { it[OAuth2ResponseParameters.Error] }

    // try error code first
    errorCode.getOrNull()?.let {
        throwOAuthError(it, contentDecodeResult.getOrThrow())
    }

    // ensure status code is successful
    if (!response.status.isSuccess()) {
        throw IOException(
            "Access token query failed with http status ${response.status} for the page $baseUrl",
        )
    }

    // will fail if content decode failed but status is OK
    val contentDecoded = contentDecodeResult.getOrThrow()

    // finally, extract access token
    return OAuthAccessTokenResponse.OAuth2(
        accessToken = contentDecoded[OAuth2ResponseParameters.AccessToken]
            ?: throw OAuth2Exception.MissingAccessToken(),
        tokenType = contentDecoded[OAuth2ResponseParameters.TokenType] ?: "",
        state = state,
        expiresIn = contentDecoded[OAuth2ResponseParameters.ExpiresIn]?.toLong() ?: 0L,
        refreshToken = contentDecoded[OAuth2ResponseParameters.RefreshToken],
        extraParameters = contentDecoded,
    )
}

internal fun decodeContent(content: String, contentType: ContentType): Parameters = when {
    contentType.match(ContentType.Application.FormUrlEncoded) -> content.parseUrlEncodedParameters()
    contentType.match(ContentType.Application.Json) -> Parameters.build {
        Json.decodeFromString(JsonObject.serializer(), content).forEach { (key, element) ->
            (element as? JsonPrimitive)?.content?.let { append(key, it) }
        }
    }

    else -> {
        // some servers may respond with a wrong content type, so we have to try to guess
        when {
            content.startsWith("{") && content.trim().endsWith("}") -> decodeContent(
                content.trim(),
                ContentType.Application.Json,
            )

            content.matches("([a-zA-Z\\d_-]+=[^=&]+&?)+".toRegex()) -> decodeContent(
                content,
                ContentType.Application.FormUrlEncoded,
            ) // TODO too risky, isn't it?
            else -> throw IOException("unsupported content type $contentType")
        }
    }
}

private fun throwOAuthError(errorCode: String, parameters: Parameters): Nothing {
    val errorDescription = parameters["error_description"] ?: "OAuth2 Server responded with $errorCode"

    throw when (errorCode) {
        "invalid_grant" -> OAuth2Exception.InvalidGrant(errorDescription)
        else -> OAuth2Exception.UnknownException(errorDescription, errorCode)
    }
}
