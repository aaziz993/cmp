package ai.tech.core.misc.plugin.serialization.model.config

import ai.tech.core.misc.type.serializer.http.ContentTypeSerial
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
public data class JsonConfig(
    val encodeDefaults: Boolean? = null,
    val explicitNulls: Boolean? = null,
    val ignoreUnknownKeys: Boolean? = null,
    val isLenient: Boolean? = null,
    val allowStructuredMapKeys: Boolean? = null,
    val prettyPrint: Boolean? = null,
    val prettyPrintIndent: String? = null,
    val coerceInputValues: Boolean? = null,
    val useArrayPolymorphism: Boolean? = null,
    val classDiscriminator: String? = null,
    val allowSpecialFloatingPointValues: Boolean? = null,
    val useAlternativeNames: Boolean? = null,
    val decodeEnumsCaseInsensitive: Boolean? = null,
    override val contentType: ContentTypeSerial = ContentType.Application.Json,
    override val enabled: Boolean = true,
) : SerializationFormatConfig
