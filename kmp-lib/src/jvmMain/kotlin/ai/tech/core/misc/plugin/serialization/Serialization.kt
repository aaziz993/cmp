package ai.tech.core.misc.plugin.serialization

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.serialization.model.config.SerializationConfig
import io.ktor.serialization.kotlinx.cbor.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.protobuf.*
import io.ktor.serialization.kotlinx.xml.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import nl.adaptivity.xmlutil.serialization.XML

@OptIn(ExperimentalSerializationApi::class)
public fun Application.configureSerialization(config: SerializationConfig?, block: (ContentNegotiationConfig.() -> Unit)? = null) {
    val configBlock: (ContentNegotiationConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {

            // JSON
            it.json?.takeIf(EnabledConfig::enabled)?.let {
                json(
                    Json {
                        it.let {
                            it.encodeDefaults?.let { encodeDefaults = it }
                            it.explicitNulls?.let { explicitNulls = it }
                            it.ignoreUnknownKeys?.let { ignoreUnknownKeys = it }
                            it.isLenient?.let { isLenient = it }
                            it.allowStructuredMapKeys?.let { allowStructuredMapKeys = it }
                            it.prettyPrint?.let { prettyPrint = it }
                            it.prettyPrintIndent?.let { prettyPrintIndent = it }
                            it.coerceInputValues?.let { coerceInputValues = it }
                            it.useArrayPolymorphism?.let { useArrayPolymorphism = it }
                            it.classDiscriminator?.let { classDiscriminator = it }
                            it.allowSpecialFloatingPointValues?.let { allowSpecialFloatingPointValues = it }
                            it.useAlternativeNames?.let { useAlternativeNames = it }
                            it.decodeEnumsCaseInsensitive?.let { decodeEnumsCaseInsensitive = it }
                        }
                    },
                    it.contentType,
                )
            }

            // XML
            it.xml?.takeIf(EnabledConfig::enabled)?.let {
                xml(
                    XML {
                        it.let {
                            it.repairNamespaces?.let { repairNamespaces = it }
                            it.xmlDeclMode?.let { xmlDeclMode = it }
                            it.indentString?.let { indentString = it }
                            it.nilAttribute?.let { nilAttribute = it }
                            it.xmlVersion?.let { xmlVersion = it }
                            it.autoPolymorphic?.let { autoPolymorphic = it }
                        }
                    },
                    it.contentType,
                )
            }

            // CBOR
            it.cbor?.takeIf(EnabledConfig::enabled)?.let {
                cbor(
                    Cbor {
                        it.let {
                            it.encodeDefaults?.let { encodeDefaults = it }
                            it.ignoreUnknownKeys?.let { ignoreUnknownKeys = it }
                        }
                    },
                    it.contentType,
                )
            }

            // PROTOBUF
            it.protobuf?.takeIf(EnabledConfig::enabled)?.let {
                protobuf(
                    ProtoBuf {
                        it.let {
                            it.encodeDefaults?.let { encodeDefaults = it }
                        }
                    },
                    it.contentType,
                )
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(ContentNegotiation) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
