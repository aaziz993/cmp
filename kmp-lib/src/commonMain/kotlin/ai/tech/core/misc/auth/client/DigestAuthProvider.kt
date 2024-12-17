package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

public class DigestAuthProvider(
    name: String?,
    httpClient: HttpClient,
    public val algorithmName: String?,
    public val realm: String?,
    keyValue: AbstractKeyValue,
) : AbstractCredentialAuthProvider(
    name,
    httpClient,
    keyValue,
) {

    override fun AuthConfig.configureAuth() {
        digest {
            this@DigestAuthProvider.algorithmName?.let { algorithmName = it }
            this@DigestAuthProvider.realm?.let { realm = it }
            credentials { getToken()?.let { DigestAuthCredentials(it.username, it.password) } }
        }
    }
}
