package ai.tech.core.misc.type.serializer

import ai.tech.core.misc.location.model.Country
import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.countries
import ai.tech.core.misc.location.model.languages
import kotlinx.serialization.Serializable

public object CountrySerializer :
    PrimitiveSerializer<Country>(
        Country::class,
        { countries[it]!!() },
        { it.alpha2 },
    )

public typealias CountrySerial = @Serializable(with = CountrySerializer::class) Country
