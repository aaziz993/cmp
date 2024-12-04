package ai.tech.core.misc.type.multiple.iterable

import ai.tech.core.misc.type.multiple.iterable.model.ContainResolution
import arrow.core.none
import kotlin.math.pow

public inline fun <T, R : Any> Iterable<T>.firstNotThrowOf(transform: (T) -> R?): R? {
    var throwable: Throwable? = null
    for (element in this) {
        try {
            return transform(element)
        }
        catch (t: Throwable) {
            throwable = t
        }
    }
    throw throwable!!
}

public inline fun <T> MutableList<T>.removeFirst(predicate: (T) -> Boolean): Int =
    indexOfFirst(predicate).also {
        if (it != -1) {
            removeAt(it)
        }
    }

public inline fun <T> MutableList<T>.removeLast(predicate: (T) -> Boolean): Int =
    indexOfLast(predicate).also {
        if (it != -1) {
            removeAt(it)
        }
    }

public fun <T> MutableList<T>.removeLast(n: Int): List<T> =
    if (isEmpty()) {
        throw NoSuchElementException("List is empty.")
    }
    else {
        (0..n).map {
            removeAt(lastIndex - n + 1 + it)
        }
    }

public inline fun <T> MutableList<T>.replaceFirst(
    predicate: (T) -> Boolean,
    replacement: T.() -> T,
): Boolean =
    indexOfFirst(predicate).let {
        if (it == -1) {
            false
        }
        else {
            this[it] = this[it].replacement()
            true
        }
    }

public inline fun <T> MutableList<T>.replace(
    elements: Collection<T>,
    equator: (T, T) -> Boolean,
): Unit = elements.forEach { e ->
    replaceFirst(
        {
            equator(it, e)
        },
    ) { e }
}

public inline fun <T> MutableList<T>.whileHasIndexItem(block: MutableList<T>.(index: Int, T) -> Unit) {
    var index = 0
    while (index < size) {
        block(index, this[index++])
    }
}

public inline fun <T> MutableList<T>.whileIsNotEmpty(block: MutableList<T>.(T) -> Unit) {
    while (isNotEmpty()) {
        block(removeAt(0))
    }
}

public fun <T> Iterable<T>.containsAll(other: Iterable<T>, equator: (thisItem: T, otherItem: T) -> Boolean): Boolean =
    other.any { otherItem -> all { thisItem -> equator(thisItem, otherItem) } }

public fun <T> Iterable<T>.containsAny(other: Iterable<T>): Boolean =
    any(other::contains)

public fun <T> Iterable<T>.containsAny(other: Iterable<T>, equator: (thisItem: T, otherItem: T) -> Boolean): Boolean =
    any { thisItem -> other.any { otherItem -> equator(thisItem, otherItem) } }

public fun <T> Iterable<T>.containsNone(other: Iterable<T>): Boolean =
    none(other::contains)

public fun <T> Iterable<T>.containsNone(other: Iterable<T>, equator: (thisItem: T, otherItem: T) -> Boolean): Boolean =
    none { thisItem -> other.any { otherItem -> equator(thisItem, otherItem) } }

public fun <T> Iterable<T>.contains(other: Iterable<T>, resolution: ContainResolution): Boolean =
    when (resolution) {
        ContainResolution.NONE -> containsNone(other)
        ContainResolution.ANY -> containsAny(other)
        ContainResolution.ALL -> all(other::contains)
    }

public fun <T> Iterable<T>.contains(other: Iterable<T>, resolution: ContainResolution, equator: (thisItem: T, otherItem: T) -> Boolean): Boolean =
    when (resolution) {
        ContainResolution.NONE -> containsNone(other, equator)
        ContainResolution.ANY -> containsAny(other, equator)
        ContainResolution.ALL -> containsAll(other, equator)
    }

public infix fun <T> Iterable<T>.symmetricDifference(other: Iterable<T>): Pair<Set<T>, Set<T>> {
    val left = this subtract other
    val right = other subtract this
    return left to right
}

public fun <T> MutableCollection<T>.symmetricDifferenceUpdate(
    other: Iterable<T>,
): Pair<Set<T>, Set<T>> =
    symmetricDifference(other).also { (left, right) ->
        removeAll(left)
        addAll(right)
    }

public inline fun <T> Iterable<T>.symmetricDifference(
    other: Iterable<T>,
    equator: (thisItem: T, otherItem: T) -> Boolean,
): Pair<Iterable<T>, Iterable<T>> {
    val left = filter { thisItem -> other.none { otherItem -> equator(thisItem, otherItem) } }
    val right = other.filter { otherItem -> none { thisItem -> equator(thisItem, otherItem) } }
    return left to right
}

public fun <T> MutableCollection<T>.symmetricDifferenceUpdate(
    other: Iterable<T>,
    equator: (thisItem: T, otherItem: T) -> Boolean,
): Pair<Iterable<T>, Iterable<T>> =
    symmetricDifference(other, equator).also { (left, right) ->
        removeAll(left)
        addAll(right)
    }

public fun <T> Collection<T>.replaceAt(index: Int, block: T.() -> T): Collection<T> =
    iterator().let { iterator ->
        List(size) {
            if (it == index) {
                iterator.next().block()
            }
            else {
                iterator.next()
            }
        }
    }

public fun <T> Collection<T>.replaceIf(predicate: (T) -> Boolean, block: T.() -> T): Collection<T> =
    iterator().let { iterator ->
        List(size) {
            val next = iterator.next()

            if (predicate(next)) {
                next.block()
            }
            else {
                next
            }
        }
    }

public fun <T> Collection<T>.replaceIfFirst(predicate: (T) -> Boolean, item: T.() -> T): Collection<T> =
    replaceAt(indexOfFirst(predicate), item)

public fun <T> Collection<T>.replaceIfLast(predicate: (T) -> Boolean, item: T.() -> T): Collection<T> =
    replaceAt(indexOfLast(predicate), item)

public fun <T> MutableCollection<T>.replaceWith(src: Collection<T>) {
    clear()
    addAll(src)
}

public fun <T> List<T>.merge(lists: List<List<T>>): List<T> =
    List(lists.size * lists[0].size) {
        this[it % lists.size]
    }

public fun <T> List<T>.unmerge(step: Int): List<List<T>> =
    List(step) { offset ->
        (indices step step).map { this[offset + it] }
    }

public fun <T> List<T>.takeIfNotEmpty(): List<T>? = takeIf(List<T>::isNotEmpty)

public fun <T : List<*>> Iterable<T>.filterNotEmpty(): Iterable<T> = filterNot(List<*>::isEmpty)

public fun <T : Map<*, *>> Iterable<T>.filterNotEmpty(): Iterable<T> = filterNot(Map<*, *>::isEmpty)

public val Iterable<Boolean>.all: Boolean
    get() = all { it }

// /////////////////////////////////////////////////////ARRAY///////////////////////////////////////////////////////////
@Suppress("UNCHECKED_CAST")
public fun <T> List<T>.toTypedArray(): Array<T> = toTypedArray<Any?>() as Array<T>

// ////////////////////////////////////////////////////NUMBER///////////////////////////////////////////////////////////
public fun List<UInt>.toUInt(base: UInt): UInt =
    foldIndexed(0U) { i, acc, v ->
        acc + v * base.toDouble().pow(i).toUInt()
    }

public fun List<UInt>.toULong(base: ULong): ULong =
    foldIndexed(0UL) { i, acc, v ->
        acc + v * base.toDouble().pow(i).toULong()
    }
