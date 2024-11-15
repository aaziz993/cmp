package ai.tech.core.misc.location.localization.weblate.client.model

import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.http.*

public interface WeblateResponse<T> {

    public val count: Int
    public val next: String?
    public val previous: String?
    public val results: Set<T>

    public val nextPage: Int?
        get() = next?.httpUrl?.parameters["page"]!!.toInt()

    public val previousPage: Int?
        get() = previous?.httpUrl?.parameters["page"]!!.toInt()
}
