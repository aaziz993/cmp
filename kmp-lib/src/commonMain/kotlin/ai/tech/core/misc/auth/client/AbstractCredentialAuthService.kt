package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.get
import ai.tech.core.misc.auth.client.model.Credential
import io.ktor.client.*

public abstract class AbstractCredentialAuthService(
    name: String?,
    httpClient: HttpClient,
    keyValue: AbstractKeyValue,
) : AbstractAuthService<Credential>(
    name,
    httpClient,
    Credential.serializer(),
    keyValue,
), CredentialAuthService {

    override suspend fun signIn(username: String, password: String): Unit = setToken(Credential(username, password))
}
