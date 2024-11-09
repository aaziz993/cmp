package ai.tech.core.misc.plugin.auth

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.jwt.ServerJWTHS256Auth
import ai.tech.core.misc.plugin.auth.jwt.ServerJWTRS256Auth
import ai.tech.core.misc.plugin.auth.jwt.model.ServerJWTConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthConfig
import ai.tech.core.misc.plugin.auth.oauth.OAuthService
import ai.tech.core.misc.plugin.auth.oauth.model.config.ServerOAuthConfig
import ai.tech.core.misc.plugin.auth.rbac.rbac
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.basic.BasicAuthService
import ai.tech.core.misc.plugin.auth.bearer.BearerAuthService
import ai.tech.core.misc.plugin.auth.digest.DigestAuthService
import ai.tech.core.misc.plugin.auth.form.FormAuthService
import ai.tech.core.misc.plugin.auth.ldap.LDAPAuthService
import io.ktor.client.*
import io.ktor.http.auth.*
import io.ktor.http.parsing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import java.nio.charset.Charset
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

public fun Application.configureAuth(
    serverURL: String,
    httpClient: HttpClient,
    config: AuthConfig?,
    block: (AuthenticationConfig.() -> Unit)? = null
) = authentication {
    config?.let {

        it.basic.forEach { (name, config) ->
            val service = BasicAuthService(name, config)

            basic(name) {
                config.realm?.let { realm = it }

                config.charset?.let { charset = Charset.forName(it) }

                validate {
                    service.validate(this, it)
                }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.digest.forEach { (name, config) ->
            val service = DigestAuthService(name, config)

            digest(name) {
                config.realm?.let { realm = it }

                config.algorithmName?.let { algorithmName = it }

                validate {
                    service.validate(this, it)
                }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.bearer.forEach { (name, config) ->
            val service = BearerAuthService(name, config)

            bearer(name) {
                config.realm?.let { realm = it }

                authenticate {
                    service.validate(this, it)
                }

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

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.form.forEach { (name, config) ->
            val service = FormAuthService(name, config)

            form(name) {
                config.userParamName?.let { userParamName = it }

                config.passwordParamName?.let { passwordParamName = it }

                challenge {
                    service.challenge(call)
                }

                validate {
                    service.validate(this, it)
                }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

//        sessionAuthServices.forEach { (name, service) ->
//            session<UserSession>(name) {
//                challenge {
//                    service.challenge(call)
//                }
//
//                validate {
//                    service.validate(it)
//                }
//
//                skipWhen { service.skip(it) }
//            }
//            rbac(name) {
//                extractRoles {
//                    // From UserIdPrincipal
//                    service.roles(it)
//                }
//            }
//        }

        it.ldap.forEach { (name, config) ->
            val service = LDAPAuthService(name, config)

            basic(name) {

                config.realm?.let { realm = it }

                config.charset?.let { charset = Charset.forName(it) }

                validate {
                    service.validate(this, it)
                }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.jwtHs256.filterValues(EnabledConfig::enable).forEach { (name, config) ->
            val service = ServerJWTHS256Auth(name, config)

            configJWT(name, config) {
                // Load the token verification config
                verifier {
                    service.jwtVerifier(it)
                }

                validate {
                    // If the token is valid, it also has the indicated audience,
                    // and has the user's field to compare it with the one we want
                    // return the JWTPrincipal, otherwise return null
                    service.validate(this, it)
                }

                challenge { defaultScheme, realm ->
                    service.challenge(call, defaultScheme, realm)
                }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.jwtRs256.filterValues(EnabledConfig::enable).forEach { (name, config) ->
            val service = ServerJWTRS256Auth(name, config)

            configJWT(name, config) {
                // Load the token verification config
                verifier(service.jwkProvider, config.issuer) {
                    acceptLeeway(3)
                }

                validate {
                    // If the token is valid, it also has the indicated audience,
                    // and has the user's field to compare it with the one we want
                    // return the JWTPrincipal, otherwise return null
                    service.validate(this, it)
                }

                challenge { defaultScheme, realm ->
                    service.challenge(call, defaultScheme, realm)
                }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.oauth.filterValues(EnabledConfig::enable).forEach { (name, config) ->
            val redirects = mutableMapOf<String, String>()

            configOAuth(
                serverURL,
                httpClient,
                name,
                config,
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
    redirectUrl: String,
    httpClient: HttpClient,
    name: String,
    config: ServerOAuthConfig,
    redirects: MutableMap<String, String>
) = with(config) {
    val service = OAuthService(name, config)

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

        skipWhen(service::skip)

        rbac(name) { roleExtractor = service::roles }
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
