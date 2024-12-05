package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*

public class DigestAuthService(
    name: String?,
    httpClient: HttpClient,
    public val algorithmName: String?,
    public val realm: String?,
    private val keyValue: AbstractKeyValue,
) : AbstractCredentialAuthService(
    name,
    httpClient,
    keyValue,
) {

    override fun AuthConfig.configureAuth() {
        digest {
            this@DigestAuthService.algorithmName?.let { algorithmName = it }
            this@DigestAuthService.realm?.let { realm = it }
            credentials { getToken()?.let { DigestAuthCredentials(it.username, it.password) } }
        }
    }
}
