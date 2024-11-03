package ai.tech.core.misc.location.serializer

import ai.tech.core.misc.location.model.Country
import ai.tech.core.misc.location.model.countries
import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import kotlinx.serialization.Serializable

public object CountrySerializer :
    PrimitiveStringSerializer<Country>(
        Country::class,
        Country::alpha2,
        { countries[it]!!() },
    )

public typealias CountrySerial = @Serializable(with = CountrySerializer::class) Country
