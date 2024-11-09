@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package ai.tech.core.misc.type

import ai.tech.core.misc.type.serializer.UuidSerializer
import ai.tech.core.misc.type.serializer.bignum.BigDecimalSerializer
import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerializer
import ai.tech.core.misc.type.serializer.subclass
import com.benasher44.uuid.Uuid
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.time.Duration
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
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.encodeToJsonElement
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
            subclass(Byte::class)
            subclass(Short::class)
            subclass(Int::class)
            subclass(Long::class)
            subclass(Float::class)
            subclass(Double::class)
            subclass(BigIntegerSerializer)
            subclass(BigDecimalSerializer)
            subclass(Char::class)
            subclass(String::class)
            subclass(LocalTime::class)
            subclass(LocalDate::class)
            subclass(LocalDateTime::class)
            subclass(Duration::class)
            subclass(DatePeriod::class)
            subclass(DateTimePeriod::class)
            subclass(UuidSerializer)
        }
    }
}

public fun Json(from: Json = json, builderAction: JsonBuilder.() -> Unit): Json = Json(from, builderAction)

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun Json.encodeAnyToJsonElement(value: Any?): JsonElement = when (value) {
    null -> JsonNull

    is JsonElement -> value

    is Boolean -> JsonPrimitive(value)

    is UByte -> JsonPrimitive(value)

    is UShort -> JsonPrimitive(value)

    is UInt -> JsonPrimitive(value)

    is ULong -> JsonPrimitive(value)

    is Byte -> JsonPrimitive(value)

    is Short -> JsonPrimitive(value)

    is Int -> JsonPrimitive(value)

    is Long -> JsonPrimitive(value)

    is Float -> JsonPrimitive(value)

    is Double -> JsonPrimitive(value)

    is Char -> JsonPrimitive(value.toString())

    is String -> JsonPrimitive(value)

    is BigInteger -> JsonPrimitive(value.toString())

    is BigDecimal -> JsonPrimitive(value.toString())

    is LocalTime -> JsonPrimitive(value.toString())

    is LocalDate -> JsonPrimitive(value.toString())

    is LocalDateTime -> JsonPrimitive(value.toString())

    is Duration -> JsonPrimitive(value.toString())

    is DatePeriod -> JsonPrimitive(value.toString())

    is DateTimePeriod -> JsonPrimitive(value.toString())

    is Uuid -> JsonPrimitive(value.toString())

    is List<*> -> JsonArray(value.map(::encodeAnyToJsonElement))

    is Map<*, *> -> JsonObject(
        value.entries.associate { it.key.toString() to encodeAnyToJsonElement(it.value) },
    )

    else -> throw IllegalArgumentException("Illegal argument type \"${value::class.simpleName}\"")
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

public fun Json.decodeAnyToString(value: String): Any? = decodeAnyFromJsonElement(decodeFromString(value))

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun <T : Any> Json.copy(serializer: KSerializer<T>, value: T, block: (Map<String, Any?>) -> Map<String, Any?> = { it }): T =
    decodeFromAny(serializer, block(encodeToAny(serializer, value) as Map<String, Any?>))

public inline fun <reified T : Any> Json.copy(value: T, noinline block: (Map<String, Any?>) -> Map<String, Any?> = { it }): T =
    copy(serializersModule.serializer(), value, block)


