package ai.tech.core.misc.type

import ai.tech.core.misc.type.invoke
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

public operator fun KFunction<*>.invoke(vararg args: Any?) = call(*args)

public operator fun KProperty<*>.invoke() = getter()

public operator fun KProperty<*>.invoke(arg: Any) = getter(arg)

private operator fun Collection<KProperty<*>>.get(propertyName: String): KProperty<*>? = find { it.name == propertyName }

public fun KClass<*>.declaredMemberProperty(propertyName: String): KProperty<*>? = declaredMemberProperties[propertyName]

public fun Any.declaredMemberPropertyValue(propertyName: String): Any? = this::class.declaredMemberProperty(propertyName)!!(this)

public fun KClass<*>.declaredMemberExtensionProperty(propertyName: String): KProperty<*>? = declaredMemberExtensionProperties[propertyName]

public fun Any.declaredMemberExtensionPropertyValue(propertyName: String): Any? = this::class.declaredMemberProperty(propertyName)!!(this)

public fun KClass<*>.memberProperty(propertyName: String): KProperty<*>? = memberProperties[propertyName]

public operator fun KClass<*>.get(propertyName: String): KProperty<*>? = memberProperty(propertyName)

public fun Any.memberPropertyValue(propertyName: String): Any? = this::class[propertyName]!!(this)

public operator fun Any.get(propertyName: String): Any? = memberPropertyValue(propertyName)

public fun KClass<*>.staticProperty(propertyName: String): KProperty<*>? = staticProperties[propertyName]

public fun Any.staticPropertyValue(propertyName: String): Any? = this::class.declaredMemberProperty(propertyName)!!()

private fun Collection<KFunction<*>>.get(funName: String, vararg argKTypes: KType): KFunction<*>? {
    val parametersSize = argKTypes.size + 1

    return find { function ->
        function.name == funName && function.parameters.size == parametersSize
        function.parameters.drop(1).map(KParameter::type).zip(argKTypes).all { (parameterType, argKType) ->
            if (parameterType.classifier is KTypeParameter) {
                (parameterType.classifier as KTypeParameter).upperBounds.contains(argKType)
            }
            else {
                parameterType == argKType
            }
        }
    }
}

public fun KClass<*>.declaredMemberFunction(funName: String, vararg argKTypes: KType): KFunction<*>? =
    declaredMemberFunctions.get(funName, *argKTypes)

public fun Any.callDeclaredMemberFunction(funName: String, vararg argKTypes: Pair<Any?, KType>): Any? =
    this::class.declaredMemberFunction(
        funName,
        *argKTypes.map(Pair<*, KType>::second)
            .toTypedArray(),
    )!!(this, *argKTypes.map(Pair<*, *>::first).toTypedArray())

public fun KClass<*>.declaredMemberExtensionFunction(funName: String, vararg argKTypes: KType): KFunction<*>? =
    declaredMemberExtensionFunctions.get(funName, *argKTypes)

public fun Any.callDeclaredMemberExtensionFunction(funName: String, vararg argKTypes: Pair<Any?, KType>): Any? =
    this::class.declaredMemberExtensionFunction(
        funName,
        *argKTypes.map(Pair<*, KType>::second)
            .toTypedArray(),
    )!!(this, *argKTypes.map(Pair<*, *>::first).toTypedArray())

public fun KClass<*>.memberFunction(funName: String, vararg argKTypes: KType): KFunction<*>? =
    memberFunctions.get(funName, * argKTypes)

public fun Any.callMemberFunction(funName: String, vararg argKTypes: Pair<Any?, KType>): Any? =
    this::class.memberFunction(
        funName,
        *argKTypes.map(Pair<*, KType>::second)
            .toTypedArray(),
    )!!(this, *argKTypes.map(Pair<*, *>::first).toTypedArray())

public operator fun Any.invoke(funName: String, vararg argKTypes: Pair<Any?, KType>): Any? = callMemberFunction(funName, *argKTypes)

public fun KClass<*>.staticFunction(funName: String, vararg argKTypes: KType): KFunction<*>? =
    staticFunctions.get(funName, *argKTypes)

public fun KClass<*>.callStaticFunction(funName: String, vararg argKTypes: Pair<Any?, KType>): Any? =
    staticFunction(
        funName,
        *argKTypes.map(Pair<*, KType>::second)
            .toTypedArray(),
    )!!(*argKTypes.map(Pair<*, *>::first).toTypedArray())

public fun KClass<*>.declaredFunction(funName: String, vararg argKTypes: KType): KFunction<*>? =
    declaredFunctions.get(funName, *argKTypes)

public fun Any.callDeclaredFunction(funName: String, vararg argKTypes: Pair<Any?, KType>): Any? =
    this::class.declaredFunction(
        funName,
        *argKTypes.map(Pair<*, KType>::second)
            .toTypedArray(),
    )!!(this, *argKTypes.map(Pair<*, *>::first).toTypedArray())

