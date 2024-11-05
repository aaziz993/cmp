package ai.tech.core.data.keyvalue

import ai.tech.core.data.keyvalue.model.registrar.KeyValueFlowRegistrar
import ai.tech.core.data.keyvalue.model.registrar.KeyValueRegistrar
import ai.tech.core.data.keyvalue.model.registrar.KeyValueTransformRegistrar
import core.type.model.TypeResolver

public inline fun <reified T> value(
    key: String? = null,
    keys: List<String>? = null,
    typeParameters: List<TypeResolver> = emptyList(),
    defaultValue: T? = null,
): KeyValueRegistrar<T> =
    KeyValueRegistrar(
        key,
        keys,
        TypeResolver(T::class, parameters = typeParameters.toTypedArray()),
        defaultValue,
    )

public inline fun <reified T> value(
    key: String? = null,
    keys: List<String>? = null,
    noinline transform: (Any?) -> T,
): KeyValueTransformRegistrar<T> =
    KeyValueTransformRegistrar(
        key,
        keys,
        transform,
    )

public inline fun <reified T> valueFlow(
    key: String? = null,
    keys: List<String>? = null,
    vararg typeParameters: TypeResolver
): KeyValueFlowRegistrar<T> =
    KeyValueFlowRegistrar(
        key,
        keys,
        TypeResolver(T::class, parameters = typeParameters)
    )
