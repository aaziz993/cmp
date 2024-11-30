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

public operator fun KFunction<*>.invoke(args: List<Any?>) = call()

private operator fun Collection<KProperty<*>>.get(propertyName: String): KProperty<*>? = find { it.name == propertyName }

public operator fun KClass<*>.get(propertyName: String): KProperty<*>? =
    memberProperties[propertyName]

public operator fun Any.get(propertyName: String): Any? = this::class[propertyName]!!.getter.call(this)

private fun Collection<KFunction<*>>.get(funName: String, vararg valueKTypes: List<KType>): KFunction<*>? {
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

public operator fun Any.invoke(funName: String, vararg argKTypes: Pair<Any?, KType>): Any? =
    this::class.memberFunctions.get(funName, argKTypes.map(Pair<*, KType>::second))!!
        .call(this, *argKTypes.map(Pair<*, *>::first).toTypedArray())
