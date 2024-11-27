package ai.tech.core.data.database

import ai.tech.core.data.database.model.config.TableConfig
import ai.tech.core.data.expression.Field
import ai.tech.core.data.expression.Value
import ai.tech.core.misc.type.kClass
import ai.tech.core.misc.type.multiple.whileIndexed
import kotlin.reflect.KClass
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes

@Suppress("UNCHECKED_CAST")
public fun <T : Any> getTables(
    kClass: KClass<T>,
    config: TableConfig,
    getReferencedTables: List<T>.(T) -> List<T>
): List<T> =
    config.packages.flatMap {
        Reflections(it).get(SubTypes.of(kClass.java).asClass<T>()).map {
            it
        }.let {
            if (config.inclusive) {
                it.filter { it.simpleName in config.names }
            }
            else {
                it.filter { it.simpleName !in config.names }
            }
        }.map {
            it.kotlin.objectInstance as T
        }
    }.sortedByForeignKeys(getReferencedTables)

private fun <T : Any> List<T>.sortedByForeignKeys(getReferencedTables: List<T>.(T) -> List<T>): List<T> {

    val (tables, dependantTables) = map { it to getReferencedTables(it).toMutableList() }
        .partition { (_, referencedTables) -> referencedTables.isEmpty() }
        .let { it.first.map(Pair<T, *>::first).toMutableList() to it.second }

    tables.whileIndexed { _, table ->
        dependantTables.forEach { (dependantTable, dependencies) ->
            if (dependencies.remove(table) && dependencies.isEmpty()) {
                tables.add(dependantTable)
            }
        }
    }

    if (tables.size != size) {
        throw IllegalStateException("Circular dependency detected among tables!")
    }

    return tables
}

internal fun Any.exp(name: String, value: Value<*>, getColumn: (name: String) -> Any): Any {
    val vkClass: KClass<*>
    val v: Any?

    when (value) {
        is Field -> getColumn(value.value).let {
            vkClass = it::class
            v = it
        }

        else -> {
            vkClass = value::class.declaredMemberProperties.find { it.name == "value" }!!.returnType.kClass
            v = value.value
        }
    }

    return this::class.memberFunctions.filter { it.name == name && it.parameters.size == 2 }.let {
        it.find { (it.parameters[1].type.classifier !is KTypeParameter) && vkClass.isSubclassOf(it.parameters[1].type.kClass) }
            ?: it.find { it.parameters[1].type.classifier is KTypeParameter }
    }!!.call(this, v)!!
}
