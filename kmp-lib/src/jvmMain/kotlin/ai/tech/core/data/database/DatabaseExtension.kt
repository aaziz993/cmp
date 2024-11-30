package ai.tech.core.data.database

import ai.tech.core.misc.type.multiple.whileIndexed
import kotlin.reflect.KClass
import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> getTables(
    kClass: KClass<T>,
    tables: Set<String> = emptySet(),
    scanPackage: String,
    excludePatterns: List<String> = emptyList(),
    getFKReferencedTables: T.(List<T>) -> List<T>
): List<T> {

    val tableObjects = tables.map(Class<*>::forName).map { it.kotlin.objectInstance as T }

    val reflections = Reflections(
        ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(scanPackage))
            .setScanners(SubTypes)
            .filterInputsBy(
                FilterBuilder()
                    .includePackage(scanPackage)
                    .apply {
                        excludePatterns.forEach(::excludePattern)
                    },
            ),
    )

    return (tableObjects + reflections.getSubTypesOf<T>(kClass.java).map { it.kotlin.objectInstance as T })
        .sortedByFKReferences(getFKReferencedTables)
}

private fun <T : Any> List<T>.sortedByFKReferences(getReferencedTables: T.(List<T>) -> List<T>): List<T> {

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
