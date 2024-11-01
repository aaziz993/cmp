package ai.tech.core.misc.type.serializer

import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.languages
import kotlinx.serialization.Serializable

public object LanguageSerializer :
    PrimitiveSerializer<Language>(
        Language::class,
        { languages[it]!!() },
        { "${it.alpha3}-${it.countryAlpha2}" }
    )


public typealias LanguageSerial = @Serializable(with = LanguageSerializer::class) Language
