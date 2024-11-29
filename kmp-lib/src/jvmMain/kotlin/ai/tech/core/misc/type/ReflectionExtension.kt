package ai.tech.core.misc.type

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.declaredMemberExtensionProperties
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties

private operator fun Collection<KProperty<*>>.get(propertyName: String): KProperty<*>? = find { it.name == propertyName }

public fun KClass<*>.declaredMemberProperty(propertyName: String): KProperty<*>? =
    declaredMemberProperties[propertyName]

public fun KClass<*>.declaredMemberExtensionProperty(propertyName: String): KProperty<*>? =
    declaredMemberExtensionProperties[propertyName]

public fun KClass<*>.memberProperty(propertyName: String): KProperty<*>? =
    memberProperties[propertyName]

public fun KClass<*>.staticProperty(propertyName: String): KProperty<*>? =
    staticProperties[propertyName]

private fun Collection<KFunction<*>>.get(funName: String, valueKTypes: List<KType>): KFunction<*>? {
    val parametersSize = valueKTypes.size + 1

    return find { function ->
        function.name == funName && function.parameters.size == parametersSize
        function.parameters.drop(1).map(KParameter::type).zip(valueKTypes).all { (parameterType, valueKType) ->
            if (parameterType.classifier is KTypeParameter) {
                (parameterType.classifier as KTypeParameter).upperBounds.contains(valueKType)
            }
            else {
                parameterType == valueKType
            }
        }
    }
}

public fun KClass<*>.declaredMemberFunction(funName: String, valueKTypes: List<KType>): KFunction<*>? =
    declaredMemberFunctions.get(funName, valueKTypes)

public fun KClass<*>.declaredMemberFunction(funName: String, vararg valueKTypes: KType): KFunction<*>? =
    declaredMemberFunction(funName, valueKTypes.toList())

public fun Any.callDeclaredMemberFunction(funName: String, valueKTypes: List<Pair<Any?, KType>>): Any? =
    this::class.declaredMemberFunction(funName, valueKTypes.map(Pair<*, KType>::second))!!
        .call(this, *valueKTypes.map(Pair<*, *>::first).toTypedArray())

public fun KClass<*>.declaredMemberExtensionFunction(funName: String, valueKTypes: List<KType>): KFunction<*>? =
    declaredMemberExtensionFunctions.get(funName, valueKTypes)

public fun KClass<*>.declaredMemberExtensionFunction(funName: String, vararg valueKTypes: KType): KFunction<*>? =
    declaredMemberExtensionFunction(funName, valueKTypes.toList())

public fun Any.callDeclaredMemberExtensionFunction(funName: String, valueKTypes: List<Pair<Any?, KType>>): Any? =
    this::class.declaredMemberExtensionFunction(funName, valueKTypes.map(Pair<*, KType>::second))!!
        .call(this, *valueKTypes.map(Pair<*, *>::first).toTypedArray())

public fun KClass<*>.memberFunction(funName: String, valueKTypes: List<KType>): KFunction<*>? =
    memberFunctions.get(funName, valueKTypes)

public fun KClass<*>.memberFunction(funName: String, vararg valueKTypes: KType): KFunction<*>? =
    memberFunction(funName, valueKTypes.toList())

public fun Any.callMemberExtensionFunction(funName: String, valueKTypes: List<Pair<Any?, KType>>): Any? =
    this::class.memberFunction(funName, valueKTypes.map(Pair<*, KType>::second))!!
        .call(this, *valueKTypes.map(Pair<*, *>::first).toTypedArray())

public fun KClass<*>.staticFunction(funName: String, valueKTypes: List<KType>): KFunction<*>? =
    staticFunctions.get(funName, valueKTypes)

public fun KClass<*>.staticFunction(funName: String, vararg valueKTypes: KType): KFunction<*>? =
    staticFunction(funName, valueKTypes.toList())

public fun Any.callStaticExtensionFunction(funName: String, valueKTypes: List<Pair<Any?, KType>>): Any? =
    this::class.staticFunction(funName, valueKTypes.map(Pair<*, KType>::second))!!
        .call(*valueKTypes.map(Pair<*, *>::first).toTypedArray())

public fun KClass<*>.declaredFunction(funName: String, valueKTypes: List<KType>): KFunction<*>? =
    declaredFunctions.get(funName, valueKTypes)

public fun KClass<*>.declaredFunction(funName: String, vararg valueKTypes: KType): KFunction<*>? =
    declaredFunction(funName, valueKTypes.toList())
