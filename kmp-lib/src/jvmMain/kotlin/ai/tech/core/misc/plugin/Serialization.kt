package ai.tech.core.misc.plugin

import com.fasterxml.jackson.databind.SerializationConfig
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
public fun Application.configSerialization(config: SerializationConfig?) {
    config?.takeIf { it.enable != false }?.let {
        install(ContentNegotiation) {

            // JSON
            it.json?.takeIf { it.enable != false }?.let {
                json(Json {
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
                }, it.contentType)
            }

            // XML
            it.xml?.takeIf { it.enable != false }?.let {
                xml(XML {
                    it.let {
                        it.repairNamespaces?.let { repairNamespaces = it }
                        it.xmlDeclMode?.let { xmlDeclMode = it }
                        it.indentString?.let { indentString = it }
                        it.nilAttribute?.let { nilAttribute = it }
                        it.xmlVersion?.let { xmlVersion = it }
                        it.autoPolymorphic?.let { autoPolymorphic = it }
                    }
                }, it.contentType)
            }

            // CBOR
            it.cbor?.takeIf { it.enable != false }?.let {
                cbor(Cbor {
                    it.let {
                        it.encodeDefaults?.let { encodeDefaults = it }
                        it.ignoreUnknownKeys?.let { ignoreUnknownKeys = it }
                    }
                }, it.contentType)
            }

            // PROTOBUF
            it.protobuf?.takeIf { it.enable != false }?.let {
                protobuf(ProtoBuf {
                    it.let {
                        it.encodeDefaults?.let { encodeDefaults = it }
                    }
                }, it.contentType)
            }
        }
    }
}
