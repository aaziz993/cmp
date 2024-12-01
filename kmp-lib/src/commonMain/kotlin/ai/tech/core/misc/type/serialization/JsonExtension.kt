@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class, ExperimentalUuidApi::class)

package ai.tech.core.misc.type.serialization

import ai.tech.core.misc.type.serialization.serializer.bignum.BigDecimalSerializer
import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerializer
import ai.tech.core.misc.type.serialization.serializer.primitive.UuidSerializer
import ai.tech.core.misc.type.serialization.serializer.primitive.primitive
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer

public val json: Json = Json {
    serializersModule = SerializersModule {
        contextual(BigIntegerSerializer)
        contextual(BigDecimalSerializer)
        contextual(UuidSerializer)

        polymorphic(Any::class) {
            primitive(Byte::class)
            primitive(Short::class)
            primitive(Int::class)
            primitive(Long::class)
            primitive(Float::class)
            primitive(Double::class)
            primitive(BigIntegerSerializer)
            primitive(BigDecimalSerializer)
            primitive(Char::class)
            primitive(String::class)
            primitive(LocalTime::class)
            primitive(LocalDate::class)
            primitive(LocalDateTime::class)
            primitive(Duration::class)
            primitive(DatePeriod::class)
            primitive(DateTimePeriod::class)
            primitive(UuidSerializer)
        }
    }
}

public fun Json(from: Json = json, builderAction: JsonBuilder.() -> Unit): Json = Json(from, builderAction)

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun Json.encodeAnyToJsonElement(value: Any?): JsonElement = when (value) {
    null -> JsonNull

    is JsonElement -> value

    is BigInteger -> JsonPrimitive(value.toString())

    is BigDecimal -> JsonPrimitive(value.toString())

    is Uuid -> JsonPrimitive(value.toString())

    is List<*> -> JsonArray(value.map(::encodeAnyToJsonElement))

    is Map<*, *> -> JsonObject(
        value.entries.associate { it.key.toString() to encodeAnyToJsonElement(it.value) },
    )

    else -> encodeToJsonElement(value::class.serializer() as KSerializer<Any>, value)
}

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun Json.decodeAnyFromJsonElement(element: JsonElement): Any? = with(element) {
    when (this) {
        JsonNull -> null

        is JsonPrimitive -> if (isString) {
            content
        }
        else {
            (booleanOrNull ?: longOrNull ?: doubleOrNull)!!
        }

        is JsonArray -> map(::decodeAnyFromJsonElement)

        is JsonObject -> mapValues { (_, v) -> decodeAnyFromJsonElement(v) }
    }
}

public fun <T> Json.encodeToAny(serializer: SerializationStrategy<T>, value: T): Any? = decodeAnyFromJsonElement(encodeToJsonElement(serializer, value))

public inline fun <reified T> Json.encodeToAny(value: T): Any? = encodeToAny(serializersModule.serializer(), value)

public fun <T> Json.decodeFromAny(deserializer: DeserializationStrategy<T>, value: Any?): T =
    decodeFromJsonElement(deserializer, encodeAnyToJsonElement(value))

public inline fun <reified T> Json.decodeFromAny(value: Any?): T = decodeFromAny(serializersModule.serializer(), value)

public fun Json.encodeAnyToString(value: Any?): String = encodeToString(encodeAnyToJsonElement(value))

public fun Json.decodeAnyFromString(deserializer: DeserializationStrategy<JsonElement>, value: String): Any? =
    decodeAnyFromJsonElement(decodeFromString(deserializer, value))

public fun Json.decodeAnyFromString(value: String): Any? = decodeAnyFromString(JsonElement::class.serializer(), value)

@Suppress("UNCHECKED_CAST")
public fun Json.decodeListFromString(value: String): List<Any?> =
    decodeAnyFromJsonElement(decodeFromString(JsonArray::class.serializer(), value)) as List<Any?>

@Suppress("UNCHECKED_CAST")
public fun Json.decodeMapFromString(value: String): Map<String, Any?> =
    decodeAnyFromJsonElement(decodeFromString(JsonObject::class.serializer(), value)) as Map<String, Any?>

// Make deep copy of an object
@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun <T : Any> Json.copy(serializer: KSerializer<T>, value: T, block: (Map<String, Any?>) -> Map<String, Any?> = { it }): T =
    decodeFromAny(serializer, block(encodeToAny(serializer, value) as Map<String, Any?>))

public inline fun <reified T : Any> Json.copy(value: T, noinline block: (Map<String, Any?>) -> Map<String, Any?> = { it }): T =
    copy(serializersModule.serializer(), value, block)

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun <T : Any> Json.create(serializer: KSerializer<T>, value: Map<String, Any?> = emptyMap()): T =
    decodeFromAny(serializer, value)

public inline fun <reified T : Any> Json.create(value: Map<String, Any?> = emptyMap()): T = create(serializersModule.serializer(), value)


