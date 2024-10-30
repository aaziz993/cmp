package ai.tech.core.misc.type

import kotlin.reflect.KClass

public open class TypeResolver(
    public val kClass: KClass<*>?,
    public vararg val parameters: TypeResolver
) {
    public operator fun get(index: Int): TypeResolver = if (index in parameters.indices) {
        parameters[index]
    } else {
        NoneTypeResolver
    }
}

public object NoneTypeResolver : TypeResolver(null)

public object AnyTypeResolver : TypeResolver(Any::class)