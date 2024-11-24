package ai.tech.core.misc.plugin.auth

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.misc.plugin.auth.jwt.JWTHS256AuthService
import ai.tech.core.misc.plugin.auth.jwt.JWTRS256AuthService
import ai.tech.core.misc.plugin.auth.jwt.model.JWTConfig
import ai.tech.core.misc.plugin.auth.model.config.AuthProvidersConfig
import ai.tech.core.misc.plugin.auth.oauth.OAuthService
import ai.tech.core.misc.plugin.auth.oauth.model.config.ServerOAuthConfig
import ai.tech.core.misc.plugin.auth.rbac.rbac
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.basic.BasicAuthService
import ai.tech.core.misc.plugin.auth.basic.model.config.BaseBasicAuthConfig
import ai.tech.core.misc.plugin.auth.database.kotysa.principal.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.database.kotysa.role.model.RoleEntity
import ai.tech.core.misc.plugin.auth.digest.DigestAuthService
import ai.tech.core.misc.plugin.auth.digest.model.config.BaseDigestAuthConfig
import ai.tech.core.misc.plugin.auth.form.FormAuthService
import ai.tech.core.misc.plugin.auth.form.model.config.BaseFormAuthConfig
import ai.tech.core.misc.plugin.auth.ldap.LDAPAuthService
import ai.tech.core.misc.plugin.auth.ldap.LDAPDigestAuthService
import ai.tech.core.misc.plugin.auth.ldap.LDAPFormAuthService
import ai.tech.core.misc.plugin.auth.model.config.RealmAuthProviderConfig
import ai.tech.core.misc.plugin.auth.session.SessionAuthService
import ai.tech.core.misc.plugin.auth.session.model.UserSession
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
    config: AuthProvidersConfig?,
    principalRepositories: Map<String?, Map<String, CRUDRepository<PrincipalEntity>>> = emptyMap(),
    roleRepositories: Map<String?, Map<String, CRUDRepository<RoleEntity>>> = emptyMap(),
    block: (AuthenticationConfig.() -> Unit)? = null
) = authentication {
    config?.takeIf(EnabledConfig::enabled)?.let {

        it.basic.forEach { (name, config) ->
            configureBasic(
                name,
                config,
                BasicAuthService(
                    name,
                    config,
                    { databaseName, tableName -> principalRepositories[databaseName]?.get(tableName) },
                    { databaseName, tableName -> roleRepositories[databaseName]?.get(tableName) },
                ),
            )
        }

        it.digest.forEach { (name, config) ->
            configureDigest(
                name,
                config,
                DigestAuthService(
                    name,
                    config,
                    { databaseName, tableName -> principalRepositories[databaseName]?.get(tableName) },
                    { databaseName, tableName -> roleRepositories[databaseName]?.get(tableName) },
                ),
            )
        }

        it.form.forEach { (name, config) ->
            configureForm(
                name,
                config,
                FormAuthService(
                    name,
                    config,
                    { databaseName, tableName -> principalRepositories[databaseName]?.get(tableName) },
                    { databaseName, tableName -> roleRepositories[databaseName]?.get(tableName) },
                ),
            )
        }

        it.ldapBasic.forEach { (name, config) -> configureBasic(name, config, LDAPAuthService(name, config)) }

        it.ldapDigest.forEach { (name, config) -> configureDigest(name, config, LDAPDigestAuthService(name, config)) }

        it.ldapForm.forEach { (name, config) -> configureForm(name, config, LDAPFormAuthService(name, config)) }

        it.jwtHs256.filterValues(EnabledConfig::enabled).forEach { (name, config) ->
            val service = JWTHS256AuthService(name, config)

            configureJWT(name, config) {
                // Load the token verification config
                verifier { service.jwtVerifier(it) }

                // If the token is valid, it also has the indicated audience,
                // and has the user's field to compare it with the one we want
                // return the JWTPrincipal, otherwise return null
                validate { service.validate(this, it) }

                challenge { defaultScheme, realm -> service.challenge(call, defaultScheme, realm) }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.jwtRs256.filterValues(EnabledConfig::enabled).forEach { (name, config) ->
            val service = JWTRS256AuthService(name, config)

            configureJWT(name, config) {
                // Load the token verification config
                verifier(service.jwkProvider, config.issuer) { acceptLeeway(3) }

                // If the token is valid, it also has the indicated audience,
                // and has the user's field to compare it with the one we want
                // return the JWTPrincipal, otherwise return null
                validate { service.validate(this, it) }

                challenge { defaultScheme, realm -> service.challenge(call, defaultScheme, realm) }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }

        it.oauth.filterValues(EnabledConfig::enabled).forEach { (name, config) ->
            configureOAuth(
                serverURL,
                httpClient,
                name,
                config,
            )
        }

        it.session.forEach { (name, config) ->
            val service = SessionAuthService(name, config)

            session<UserSession>(name) {
                challenge { service.challenge(call) }

                validate { service.validate(this, it) }

                skipWhen(service::skip)
            }

            rbac(name) { roleExtractor = service::roles }
        }
    }

    block?.invoke(this)
}

private fun <C, S> AuthenticationConfig.configureBasic(
    name: String,
    config: C,
    service: S,
) where C : BaseBasicAuthConfig, C : RealmAuthProviderConfig, S : AuthProvider, S : ValidateAuthProvider<UserPasswordCredential> {
    basic(name) {
        config.realm?.let { realm = it }

        config.charset?.let { charset = Charset.forName(it) }

        validate { service.validate(this, it) }

        skipWhen(service::skip)
    }

    rbac(name) { roleExtractor = service::roles }
}

private fun <C, S> AuthenticationConfig.configureDigest(
    name: String,
    config: C,
    service: S,
) where C : BaseDigestAuthConfig, C : RealmAuthProviderConfig, S : AuthProvider, S : DigestAuthProvider, S : ValidateAuthProvider<DigestCredential> {
    digest(name) {
        config.realm?.let { realm = it }

        config.algorithmName?.let { algorithmName = it }

        digestProvider(service::digestProvider)

        validate { service.validate(this, it) }

        skipWhen(service::skip)
    }

    rbac(name) { roleExtractor = service::roles }
}

private fun <C, S> AuthenticationConfig.configureForm(
    name: String,
    config: C,
    service: S,
) where C : BaseFormAuthConfig, S : AuthProvider, S : ValidateAuthProvider<UserPasswordCredential>, S : ChallengeAuthProvider {
    form(name) {
        config.userParamName?.let { userParamName = it }

        config.passwordParamName?.let { passwordParamName = it }

        challenge { service.challenge(call) }

        validate { service.validate(this, it) }

        skipWhen(service::skip)
    }

    rbac(name) { roleExtractor = service::roles }
}

private fun AuthenticationConfig.configureJWT(
    name: String,
    config: JWTConfig,
    configure: JWTAuthenticationProvider.Config.() -> Unit
) {
    jwt(name) {
        // With realm, we can get the token from the request
        realm = config.realm

        config.authHeader?.let { header -> authHeader { it.authHeader(header) } }

        config.authSchemes?.let {
            authSchemes(it.defaultScheme, *it.additionalSchemes.toTypedArray())
        }

        configure()
    }
}

private fun AuthenticationConfig.configureOAuth(
    redirectUrl: String,
    httpClient: HttpClient,
    name: String,
    config: ServerOAuthConfig,
) = with(config) {
    val redirects = mutableMapOf<String, String>()

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
