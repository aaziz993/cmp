package ai.tech.core.data.database

import ai.tech.core.data.database.model.config.TableConfig
import ai.tech.core.misc.type.multiple.whileIndexed
import kotlin.reflect.KClass
import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> getTables(
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
