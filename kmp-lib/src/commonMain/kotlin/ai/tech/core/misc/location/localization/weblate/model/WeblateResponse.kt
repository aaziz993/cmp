package ai.tech.core.misc.location.localization.weblate.model

import io.ktor.http.*

public interface WeblateResponse<T> {

    public val count: Int
    public val next: String?
    public val previous: String?
    public val results: Set<T>
}
