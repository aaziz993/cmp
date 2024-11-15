package ai.tech.core.misc.location.localization.weblate.client

import ai.tech.core.misc.location.localization.weblate.client.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.localization.weblate.client.model.WeblateUnitsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryName

internal interface WeblateApi {

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
