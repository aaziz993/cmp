package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.model.Credential
import io.ktor.client.*

public abstract class AbstractCredentialAuthProvider(
    name: String?,
    httpClient: HttpClient,
    keyValue: AbstractKeyValue,
) : AbstractAuthProvider<Credential>(
    name,
    httpClient,
    Credential.serializer(),
    keyValue,
), CredentialAuthProvider {

    override suspend fun signIn(username: String, password: String): Unit = setToken(Credential(username, password))
}
