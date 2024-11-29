package ai.tech.core.data.database

import ai.tech.core.data.database.model.config.Creation
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
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
    create: Creation = Creation.IF_NOT_EXISTS,
    createInBatch: Boolean = false,
    getReferencedTables: T.(List<T>) -> List<T>
): List<T> =
    packages.flatMap {
        Reflections(it).get(SubTypes.of(kClass.java).asClass<T>()).map {
            it
        }.let {
            if (inclusive) {
                it.filter { it.simpleName in names }
            }
            else {
                it.filter { it.simpleName !in names }
            }
        }.map {
            it.kotlin.objectInstance as T
        }
    }.sortedByForeignKeys(getReferencedTables)

private fun <T : Any> List<T>.sortedByForeignKeys(getReferencedTables: T.(List<T>) -> List<T>): List<T> {

    val (tables, dependantTables) = map { it to it.getReferencedTables(this).toMutableList() }
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
