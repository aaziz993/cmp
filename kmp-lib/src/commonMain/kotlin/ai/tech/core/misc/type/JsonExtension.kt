@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package ai.tech.core.misc.type

import ai.tech.core.misc.type.multiple.map
import ai.tech.core.misc.type.multiple.toList
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.encodeToString
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

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun <T> Json.toGeneric(element: JsonElement, type: TypeResolver): T = when (element) {
    JsonNull -> null

    is JsonPrimitive -> when (type.kClass) {
        null -> if (element.isString) {
            element.content
        } else {
            listOfNotNull(
                element.booleanOrNull,
                element.longOrNull,
                element.doubleOrNull,
            ).firstOrNull()!!
        }

        Boolean::class -> element.content.toBoolean()
        Byte::class -> element.content.toByte()
        Short::class -> element.content.toShort()
        Int::class -> element.content.toInt()
        Long::class -> element.content.toLong()
        Float::class -> element.content.toFloat()
        Double::class -> element.content.toDouble()
        BigInteger::class -> BigInteger.parseString(element.content)
        BigDecimal::class -> BigDecimal.parseString(element.content)
        String::class -> element.content
        else -> decodeFromString(type.kClass.serializer(), "\"${element.content}\"")
    }

    is JsonArray -> element.map { toGeneric(it, type[0]) as Any }.let {
        when (type.kClass) {
            null -> it
            Array::class -> it.toTypedArray()
            List::class -> it
            Set::class -> it.toSet()
            Iterator::class -> it.iterator()
            Sequence::class -> it.asSequence()
            else -> IllegalArgumentException("Unknown type \"${type.kClass.simpleName}}\"")
        }
    }

    is JsonObject -> when (type.kClass) {
        null, Map::class -> element.mapValues { (_, v) -> toGeneric(v, type[1]) as Any }
        Any::class -> decodeFromJsonElement(PolymorphicSerializer(type.kClass), element)
        else -> decodeFromJsonElement(type.kClass.serializer(), element)
    }
} as T

public inline fun <reified T> Json.toGeneric(
    element: JsonElement, typeParameters: List<TypeResolver> = emptyList()
): T = toGeneric(element, TypeResolver(T::class, * typeParameters.toTypedArray()))

public fun Json.toGeneric(value: Any?, valueType: TypeResolver, genericType: TypeResolver): Any? =
    toGeneric(toJsonElement(value, valueType), genericType)

public fun Json.toGeneric(
    value: Any,
    valueTypeParameters: List<TypeResolver> = emptyList(),
    genericType: TypeResolver,
): Any = toGeneric(
    value,
    TypeResolver(value::class, *valueTypeParameters.toTypedArray()),
    genericType
) as Any

public inline fun <reified R : Any> Json.toGeneric(
    value: Any,
    valueTypeParameters: List<TypeResolver> = emptyList(),
    genericTypeParameters: List<TypeResolver> = emptyList(),
): R = toGeneric(
    value,
    valueTypeParameters,
    TypeResolver(R::class, *genericTypeParameters.toTypedArray()),
) as R

public inline fun <reified T, reified R> Json.toGeneric(
    value: T,
    valueTypeParameters: List<TypeResolver> = emptyList(),
    genericTypeParameters: List<TypeResolver> = emptyList(),
): R = toGeneric(
    value,
    TypeResolver(T::class, *valueTypeParameters.toTypedArray()),
    TypeResolver(R::class, *genericTypeParameters.toTypedArray())
) as R

public fun <K, V> Json.toMap(value: Any): Map<K, V> = toGeneric<Map<K, V>>(value)

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
public fun Json.toJsonElement(value: Any?, type: TypeResolver): JsonElement = when (value) {
    is JsonElement -> value
    is Array<*> -> JsonArray(value.map { this@toJsonElement.toJsonElement(it, type[0]) })
    is Iterable<*> -> JsonArray(value.map { this@toJsonElement.toJsonElement(it, type[0]) })
    is Iterator<*> -> JsonArray(value.map { this@toJsonElement.toJsonElement(it, type[0]) }.toList())
    is Sequence<*> -> JsonArray(value.map { this@toJsonElement.toJsonElement(it, type[0]) }.toList())
    is Map<*, *> -> JsonObject(value.entries.associate {
        it.key.toString() to this@toJsonElement.toJsonElement(
            it.value, type[1]
        )
    })

    else -> value?.let {
        when (type.kClass) {
            null -> encodeToJsonElement(value::class.serializer() as KSerializer<Any>, value)
            Any::class -> encodeToJsonElement(PolymorphicSerializer(type.kClass as KClass<Any>), value)
            else -> encodeToJsonElement(type.kClass.serializer() as KSerializer<Any>, value)
        }
    } ?: JsonNull
}

public inline fun <reified T> Json.toJsonElement(
    value: T, typeParameters: List<TypeResolver> = emptyList()
): JsonElement = toJsonElement(value, TypeResolver(T::class, *typeParameters.toTypedArray()))

public fun Json.encode(value: Any?, type: TypeResolver): String = encodeToString(toJsonElement(value, type))

public inline fun <reified T> Json.encode(value: T, typeParameters: List<TypeResolver> = emptyList()): String =
    encode(value, TypeResolver(T::class, *typeParameters.toTypedArray()))

public fun <T> Json.decode(element: JsonElement, type: TypeResolver): T = toGeneric(element, type)

public inline fun <reified T> Json.decode(
    element: JsonElement, typeParameters: List<TypeResolver> = emptyList()
): T = decode(element, TypeResolver(T::class, * typeParameters.toTypedArray()))

public fun <T> Json.decode(value: Any?, type: TypeResolver): T = decode(toJsonElement(value), type)

public inline fun <reified T> Json.decode(value: Any?, typeParameters: List<TypeResolver> = emptyList()): T =
    decode(value, TypeResolver(T::class, * typeParameters.toTypedArray()))

public fun <T> Json.decode(value: String, type: TypeResolver): T = decode(decodeFromString<JsonElement>(value), type)

public inline fun <reified T> Json.decode(value: String, typeParameters: List<TypeResolver> = emptyList()): T =
    decode(value, TypeResolver(T::class, *typeParameters.toTypedArray()))

@Suppress("UNCHECKED_CAST")
public fun <T : Any> Json.create(map: Map<String, Any?> = emptyMap(), type: TypeResolver): T =
    toGeneric(map, genericType = type) as T

public inline fun <reified T : Any> Json.create(map: Map<String, Any?> = emptyMap()): T =
    create(map, TypeResolver(T::class))

public inline fun <T : Any> Json.copy(value: T, onMap: (Map<String, Any?>) -> Map<String, Any?> = { it }): T =
    create(onMap(toMap(value)), TypeResolver(value::class))

public fun <T : Any> Json.copy(value: T, map: Map<String, Any?>): T =
    copy(value) { it + map }