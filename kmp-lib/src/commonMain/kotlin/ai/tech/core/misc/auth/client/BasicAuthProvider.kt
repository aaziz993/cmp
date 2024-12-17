package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

public class BasicAuthProvider(
    name: String?,
    httpClient: HttpClient,
    public val realm: String?,
    keyValue: AbstractKeyValue,
) : AbstractCredentialAuthProvider(
    name,
    httpClient,
    keyValue,
) {

    override fun AuthConfig.configureAuth() {
        basic {
            this@BasicAuthProvider.realm?.let { realm = it }
            credentials { getToken()?.let { BasicAuthCredentials(it.username, it.password) } }
        }
    }
}
