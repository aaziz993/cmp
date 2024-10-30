package ai.tech.core.data.location.weblate.model

import io.ktor.http.*

public interface WeblateResponse<T> {
    public val count: Int
    public val next: String?
    public val previous: String?
    public val results: Set<T>

    public val nextPath: String?
        get() = next?.let { Url(it).fullPath }
}