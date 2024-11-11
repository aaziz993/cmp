package ai.tech.core.misc.type.multiple

import kotlin.math.pow

public inline fun <T, R : Any> Iterable<T>.firstNotThrowOf(transform: (T) -> R?): R? {
    var throwable: Throwable? = null
    for (element in this) {
        try {
            return transform(element)
        } catch (t: Throwable) {
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

public inline fun <T> MutableList<T>.removeLast(n: Int): List<T> =
    if (isEmpty()) {
        throw NoSuchElementException("List is empty.")
    } else {
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
        } else {
            this[it] = this[it].replacement()
            true
        }
    }

public inline fun <T> MutableList<T>.replace(
    elements: Collection<T>,
    equator: (T, T) -> Boolean,
): Unit = elements.forEach { e ->
    replaceFirst({
        equator(it, e)
    }) { e }
}

public inline fun <T> MutableList<T>.whileIndexed(block: MutableList<T>.(Int, T) -> Unit) {
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

public inline fun <T> Iterable<T>.outersect(
    other: Collection<T>,
    equator: (T, T) -> Boolean = { v1, v2 -> v1 == v2 },
): Pair<List<T>, List<T>> {
    val left = filter { v -> other.none { equator(v, it) } }
    val right = other.filter { v -> none { equator(v, it) } }
    return left to right
}

public inline fun <T> MutableCollection<T>.outersectUpdate(
    other: Collection<T>,
    noinline equator: (T, T) -> Boolean = { v1, v2 -> v1 == v2 },
): Pair<List<T>, List<T>> {
    val p = outersect(other, equator)
    removeAll { v -> p.first.any { equator(v, it) } }
    addAll(p.second)
    return p
}

public fun <T> Collection<T>.replaceAt(index: Int, item: T.() -> T): List<T> = iterator().let { iterator ->
    List(size) {
        if (it == index) {
            iterator.next().item()
        } else {
            iterator.next()
        }
    }
}

public fun <T> List<T>.merge(lists: List<List<T>>): List<T> =
    List(lists.size * lists[0].size) {
        this[it % lists.size]
    }

public fun <T> List<T>.unmerge(step: Int): List<List<T>> =
    List(step) { offset ->
        (indices step step).map { this[offset + it] }
    }

public val Iterable<Boolean>.all: Boolean
    get() = all { it }

// ////////////////////////////////////////////////////NUMBER///////////////////////////////////////////////////////////
public inline fun List<UInt>.toUInt(base: UInt): UInt =
    foldIndexed(0U) { i, acc, v ->
        acc + v * base.toDouble().pow(i).toUInt()
    }

public inline fun List<UInt>.toULong(base: ULong): ULong =
    foldIndexed(0UL) { i, acc, v ->
        acc + v * base.toDouble().pow(i).toULong()
    }
