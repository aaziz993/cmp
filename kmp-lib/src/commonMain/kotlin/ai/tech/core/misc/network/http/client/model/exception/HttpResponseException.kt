package ai.tech.core.misc.network.http.client.model.exception

public class HttpResponseException(public val errorInfo: ErrorInfo) : Throwable(errorInfo.error.message)
