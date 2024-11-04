package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.auth.server.jwt.ServerJWTHS256Auth
import ai.tech.core.misc.auth.server.jwt.ServerJWTRS256Auth
import ai.tech.core.misc.auth.server.jwt.model.ServerJWTConfig
import ai.tech.core.misc.auth.server.model.config.ServerAuthConfig
import ai.tech.core.misc.auth.server.oauth.ServerOAuth
import ai.tech.core.misc.auth.server.oauth.model.config.ServerOAuthConfig
import ai.tech.core.misc.auth.server.rbac.rbac
import io.ktor.client.*
import io.ktor.http.auth.*
import io.ktor.http.parsing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

public fun Application.configureAuth(
    address: String,
    httpClient: HttpClient,
    config: ServerAuthConfig?,
    block: (AuthenticationConfig.() -> Unit)? = null
) = authentication {
    config?.let {
        val redirects = mutableMapOf<String, String>()
        it.jwtHs256.forEach { (name, config) ->
            val jwtHS256Auth = ServerJWTHS256Auth(name, config)
            configJWT(name, config) {
                // Load the token verification config
                verifier {
                    jwtHS256Auth.jwtVerifier(it)
                }

                validate {
                    // If the token is valid, it also has the indicated audience,
                    // and has the user's field to compare it with the one we want
                    // return the JWTPrincipal, otherwise return null
                    jwtHS256Auth.validate(this, it)
                }

                challenge { defaultScheme, realm ->
                    jwtHS256Auth.challenge(call, defaultScheme, realm)
                }

                skipWhen { jwtHS256Auth.skip(it) }

            }
            rbac(name) {
                roleExtractor = { jwtHS256Auth.roles(it as JWTPrincipal) }
            }
        }

        it.jwtRs256.forEach { (name, config) ->
            val jwtRS256Auth = ServerJWTRS256Auth(name, config)
            configJWT(name, config) {
                // Load the token verification config
                verifier(jwtRS256Auth.jwkProvider, config.issuer) {
                    acceptLeeway(3)
                }

                validate {
                    // If the token is valid, it also has the indicated audience,
                    // and has the user's field to compare it with the one we want
                    // return the JWTPrincipal, otherwise return null
                    jwtRS256Auth.validate(this, it)
                }

                challenge { defaultScheme, realm ->
                    jwtRS256Auth.challenge(call, defaultScheme, realm)
                }

                skipWhen {
                    jwtRS256Auth.skip(it)
                }
            }
            rbac(name) {
                roleExtractor = { jwtRS256Auth.roles(it as JWTPrincipal) }
            }
        }

        it.keycloak.forEach { (name, config) ->
            configOAuth(
                name,
                "keycloak",
                address,
                config,
                httpClient,
                redirects,
            )
        }

        it.github.forEach { (name, config) -> configOAuth(name, "github", address, config, httpClient, redirects) }

        it.google.forEach { (name, config) -> configOAuth(name, "google", address, config, httpClient, redirects) }

        it.facebook.forEach { (name, config) ->
            configOAuth(
                name,
                "facebook",
                address,
                config,
                httpClient,
                redirects,
            )
        }

    }

    block?.invoke(this)
}

private fun AuthenticationConfig.configJWT(
    name: String,
    config: ServerJWTConfig,
    configure: JWTAuthenticationProvider.Config.() -> Unit
) {
    jwt(name) {
        // With realm, we can get the token from the request
        realm = config.realm

        config.authHeader?.let { header ->
            authHeader {
                it.authHeader(header)
            }
        }

        config.authSchemes?.let {
            authSchemes(
                it.defaultScheme,
                *it.additionalSchemes.toTypedArray(),
            )
        }

        configure()
    }
}

private fun AuthenticationConfig.configOAuth(
    name: String,
    provider: String,
    redirectUrl: String,
    config: ServerOAuthConfig,
    httpClient: HttpClient,
    redirects: MutableMap<String, String>
) = with(config) {
    val oauth = ServerOAuth(name, config)
    oauth(name) {
        urlProvider = { "$redirectUrl/callback" }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = provider,

                clientId = clientId,
                clientSecret = clientSecret,
                authorizeUrl = authorizeUrl,
                accessTokenUrl = accessTokenUrl,
                accessTokenRequiresBasicAuth = accessTokenRequiresBasicAuth,
                requestMethod = requestMethod,
                defaultScopes = defaultScopes,
                extraAuthParameters = extraAuthParameters,
                onStateCreated = { call, state ->
                    //saves new state with redirect url value
                    call.request.queryParameters["redirectUrl"]?.let {
                        redirects[state] = it
                    }
                },
            )
        }
        client = httpClient

        skipWhen { oauth.skip(it) }

        rbac(name) {
            roleExtractor = {
                oauth.roles(it)
            }
        }
    }
}

private fun ApplicationCall.authHeader(header: String) = request.headers[header]?.let {
    try {
        parseAuthorizationHeader(it)
    }
    catch (cause: ParseException) {
        throw BadRequestException("Invalid auth header", cause)
    }
}
