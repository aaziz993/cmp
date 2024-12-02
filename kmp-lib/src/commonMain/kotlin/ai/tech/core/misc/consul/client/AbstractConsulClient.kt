package ai.tech.core.misc.consul.client

import ai.tech.core.misc.network.http.client.AbstractApiHttpClient
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*

public abstract class AbstractConsulClient(
    httpClient: HttpClient,
    public val address: String,
    public val aclToken: String? = null
) : AbstractApiHttpClient(httpClient, address) {

    final override fun DefaultRequest.DefaultRequestBuilder.configureDefaultRequest() {
        if (aclToken != null) {
            header(HttpHeaders.Authorization, "Bearer $aclToken")
        }
    }
}



