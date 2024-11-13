package ai.tech.core.misc.location.localization.weblate

import ai.tech.core.misc.location.localization.weblate.model.WeblateTranslationsResponse
import ai.tech.core.misc.location.localization.weblate.model.WeblateUnitsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.QueryName

internal interface WeblateApi {

    @GET("api/translations/?format=json")
    suspend fun getTranslations(
        @QueryName format: String? = null,
        @QueryName page: Int? = null,
    ): WeblateTranslationsResponse

    @GET("api/units/?format=json")
    suspend fun getUnits(
        @QueryName format: String? = null,
        @QueryName page: Int? = null,
    ): WeblateUnitsResponse
}
