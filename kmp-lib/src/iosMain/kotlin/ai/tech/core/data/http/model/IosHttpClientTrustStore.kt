package ai.tech.core.data.http.model

import ai.tech.core.data.http.model.client.HttpClientTrustStore
import io.ktor.client.engine.darwin.ChallengeHandler
import platform.Foundation.NSURLAuthenticationChallenge
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionAuthChallengeDisposition
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.NSURLSessionTask

public class IosHttpClientTrustStore(private val urlCredential: NSURLCredential) : ChallengeHandler {

    override fun invoke(
        session: NSURLSession,
        task: NSURLSessionTask,
        challenge: NSURLAuthenticationChallenge,
        completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential?) -> Unit
    ) {
        completionHandler(NSURLSessionAuthChallengeUseCredential, urlCredential)
    }
}