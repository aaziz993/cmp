package ai.tech.core.misc.location.localization.weblate.client.model

import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.http.*

public interface WeblateResponse<T> {

    public val count: Int
    public val next: String?
    public val previous: String?
    public val results: Set<T>
}
