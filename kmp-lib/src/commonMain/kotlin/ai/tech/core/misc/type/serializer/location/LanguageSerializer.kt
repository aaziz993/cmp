package ai.tech.core.misc.type.serializer.location

import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.languages
import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import kotlin.collections.get
import kotlinx.serialization.Serializable

public object LanguageSerializer :
    PrimitiveStringSerializer<Language>(
        Language::class,
        Language::toString,
        { languages[it]!!() },
    )

public typealias LanguageSerial = @Serializable(with = LanguageSerializer::class) Language
