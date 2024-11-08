@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package ai.tech.core.misc.type

import ai.tech.core.misc.type.multiple.map
import ai.tech.core.misc.type.multiple.toList
import ai.tech.core.misc.type.serializer.PolymorphicPrimitiveSerializer
import ai.tech.core.misc.type.serializer.UuidSerializer
import ai.tech.core.misc.type.serializer.bignum.BigDecimalSerializer
import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerializer
import com.benasher44.uuid.Uuid
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

public val json: Json = Json {
    serializersModule = SerializersModule {
        contextual(BigInteger::class, BigIntegerSerializer)
        contextual(BigDecimal::class, BigDecimalSerializer)
        contextual(Uuid::class, UuidSerializer)

        polymorphic(Any::class) {
            subclass(Byte::class, PolymorphicPrimitiveSerializer(Byte::class.serializer()))
            subclass(Short::class, PolymorphicPrimitiveSerializer(Short::class.serializer()))
            subclass(Int::class, PolymorphicPrimitiveSerializer(Int::class.serializer()))
            subclass(Long::class, PolymorphicPrimitiveSerializer(Long::class.serializer()))
            subclass(Float::class, PolymorphicPrimitiveSerializer(Float::class.serializer()))
            subclass(Double::class, PolymorphicPrimitiveSerializer(Double::class.serializer()))
            subclass(BigInteger::class, PolymorphicPrimitiveSerializer(BigIntegerSerializer))
            subclass(BigDecimal::class, PolymorphicPrimitiveSerializer(BigDecimalSerializer))
            subclass(Char::class, PolymorphicPrimitiveSerializer(Char::class.serializer()))
            subclass(String::class, PolymorphicPrimitiveSerializer(String::class.serializer()))
            subclass(LocalTime::class, PolymorphicPrimitiveSerializer(LocalTime::class.serializer()))
            subclass(LocalDate::class, PolymorphicPrimitiveSerializer(LocalDate::class.serializer()))
            subclass(LocalDateTime::class, PolymorphicPrimitiveSerializer(LocalDateTime::class.serializer()))
            subclass(Duration::class, PolymorphicPrimitiveSerializer(Duration::class.serializer()))
            subclass(DatePeriod::class, PolymorphicPrimitiveSerializer(DatePeriod::class.serializer()))
            subclass(DateTimePeriod::class, PolymorphicPrimitiveSerializer(DateTimePeriod::class.serializer()))
            subclass(Uuid::class, UuidSerializer)
        }
    }
}

public fun Json(from: Json = json, builderAction: JsonBuilder.() -> Unit): Json = Json(from, builderAction)

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

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun Json.encodeAnyToJsonElement(value: Any?): JsonElement = when (value) {
    null -> JsonNull

    is JsonElement -> value

    is List<*> -> JsonArray(value.map(::encodeAnyToJsonElement))

    is Map<*, *> -> JsonObject(
        value.entries.associate { it.key.toString() to encodeAnyToJsonElement(it.value) },
    )

    else -> encodeToJsonElement(value::class.serializer() as KSerializer<Any>, value)
}

@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any> Json.create(value: T? = null, block: (Map<String, Any?>) -> Map<String, Any?> = { it }): T =
    decodeFromJsonElement(
        T::class.serializer(),
        encodeAnyToJsonElement(block(value?.let { decodeAnyFromJsonElement(encodeToJsonElement(it)) as Map<String, *> }.orEmpty())),
    )
