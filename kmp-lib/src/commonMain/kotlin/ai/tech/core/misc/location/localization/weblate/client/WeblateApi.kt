package ai.tech.core.misc.location.localization.weblate.client

import ai.tech.core.misc.location.localization.weblate.client.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.localization.weblate.client.model.WeblateUnitsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryName
import io.ktor.client.statement.HttpStatement

internal interface WeblateApi {

    @GET("{path}")
    suspend fun get(
        @Path("path") path: String,
        @QueryName format: String? = null,
        @QueryName page: Int? = null,
    ): HttpStatement

    @GET("api/translations")
    suspend fun getTranslations(
        @QueryName format: String? = null,
        @QueryName page: Int? = null,
    ): WeblateTranslationsResponse

    @GET("api/units")
    suspend fun getUnits(
        @QueryName format: String? = null,
        @QueryName page: Int? = null,
    ): WeblateUnitsResponse
}
