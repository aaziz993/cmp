package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.multiple.model.AsyncIterator
import ai.tech.core.misc.type.multiple.model.ClosableAbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.ClosableAbstractIterator
import okio.Buffer
import okio.Source
import okio.Timeout
import kotlin.properties.Delegates

public fun <T> Iterator<T>.next(
    count: Int,
    element: (Int, T) -> Unit,
): Int {
    var index = 0
    while (hasNext() && index < count) {
        element(index++, next())
    }
    return index
}

public fun <T> Iterator<T>.next(count: Int): List<T> =
    mutableListOf<T>().also { list ->
        next(count) { _, e ->
            list.add(e)
        }
    }

public fun emptyIterator(): Iterator<Nothing> = EmptyIterator

public object EmptyIterator : Iterator<Nothing> {
    override fun hasNext(): Boolean = false

    override fun next(): Nothing = throw IllegalStateException("Iterator is empty")
}

public fun <T> concat(vararg iterators: Iterator<T>): Iterator<T> = IteratorsConcat(*iterators)

private class IteratorsConcat<T>(
    private vararg val iterators: Iterator<T>,
) : AbstractIterator<T>() {
    private var index = 0

    override fun computeNext() {
        do {
            if (iterators[index].hasNext()) {
                setNext(iterators[index].next())
                return
            }
        } while (++index < iterators.size)
        done()
    }
}

public fun <T> merge(iterators: List<Iterator<T>>): Iterator<T> = IteratorsMerge(iterators)

private class IteratorsMerge<T>(
    private val iterators: List<Iterator<T>>,
) : Iterator<T> {
    private var index = 0

    override fun hasNext(): Boolean = iterators[0].hasNext()

    override fun next(): T =
        iterators[index].next().also {
            if (++index >= iterators.size) {
                index = 0
            }
        }
}

public fun <T> Iterator<T>.drop(n: Int): Iterator<T> {
    for (i in 0 until n) {
        if (hasNext()) {
            next()
        } else {
            throw IllegalStateException("Not enough elements to drop")
        }
    }
    return this
}

public fun <T> Iterator<T>.depthIterator(
    transform: IteratorDepthIterator<T>.(Int, T) -> Iterator<T>?,
    removeLast: () -> Unit = {},
): Iterator<T> = IteratorDepthIterator(this, transform, removeLast)

public class IteratorDepthIterator<T>(
    iterator: Iterator<T>,
    private val transform: IteratorDepthIterator<T>.(Int, T) -> Iterator<T>?,
    private val removeLast: () -> Unit,
) : AbstractIterator<T>() {
    private val iterators = mutableListOf(iterator)
    private var isStop: Boolean = false

    public fun stop() {
        isStop = true
    }

    override fun computeNext() {
        do {
            val last = iterators.last()
            if (last.hasNext()) {
                val next = last.next()

                val transformed = transform(iterators.size - 1, next)

                if (isStop) {
                    break
                }

                if (transformed == null) {
                    setNext(next)
                    return
                } else {
                    iterators.add(transformed)
                }
            } else {
                iterators.removeLast()
                removeLast()
            }
        } while (iterators.size > 0)

        done()
    }
}

public fun <T> Iterator<T>.breadthIterator(
    transform: IteratorBreadthIterator<T>.(Int, T) -> Iterator<T>?,
    removeFirst: () -> Unit = {},
): Iterator<T> = IteratorBreadthIterator(this, transform, removeFirst)

public class IteratorBreadthIterator<T>(
    iterator: Iterator<T>,
    private val transform: IteratorBreadthIterator<T>.(Int, T) -> Iterator<T>?,
    private val removeFirst: () -> Unit,
) : AbstractIterator<T>() {
    private val iterators = mutableListOf(iterator)
    private var isStop: Boolean = false

    public fun stop() {
        isStop = true
    }

    override fun computeNext() {
        do {
            val first = iterators.first()
            if (first.hasNext()) {
                val next = first.next()

                val transformed = transform(iterators.size - 1, next)

                if (isStop) {
                    break
                }

                if (transformed == null) {
                    setNext(next)
                    return
                } else {
                    iterators.add(transformed)
                }
            } else {
                iterators.removeFirst()
                removeFirst()
            }
        } while (iterators.size > 0)

        done()
    }
}

public fun <T> Iterator<T>.filter(predicate: (T) -> Boolean): Iterator<T> = IteratorFilter(this, predicate)

private class IteratorFilter<T>(
    private val iterator: Iterator<T>,
    private val predicate: (T) -> Boolean,
) : AbstractIterator<T>() {
    override fun computeNext() {
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (predicate(item)) {
                setNext(item)
                return
            }
        }
        done()
    }
}

public fun <T, R> Iterator<T>.map(transform: (T) -> R): Iterator<R> = IteratorMap(this, transform)

private class IteratorMap<T, R>(
    private val iterator: Iterator<T>,
    private val transform: (T) -> R,
) : Iterator<R> {
    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): R = transform(iterator.next())
}

public fun <T, R> Iterator<T>.mapIndexed(transform: (index: Int, T) -> R): Iterator<R> =
    IteratorMapIndexed(this, transform)

private class IteratorMapIndexed<T, R>(
    private val iterator: Iterator<T>,
    private val transform: (index: Int, T) -> R,
) : Iterator<R> {
    private var index = 0

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): R = transform(index++, iterator.next())
}

public fun <T, R> Iterator<T>.flatMap(transform: (T) -> Iterator<R>): Iterator<R> = IteratorFlatMap(this, transform)

private class IteratorFlatMap<T, R>(
    private val iterator: Iterator<T>,
    private val transform: (T) -> Iterator<R>,
) : AbstractIterator<R>() {
    private var item: Iterator<R> = EmptyIterator

    override fun computeNext() {
        if (item.hasNext()) {
            setNext(item.next())
        } else {
            if (iterator.hasNext()) {
                item = transform(iterator.next())
                if(item.hasNext()) {
                    setNext(item.next())
                    return
                }
            }
            done()

        }
    }
}

public fun <T> Iterator<T>.startsWith(vararg elements: T): Boolean =
    elements.all {
        if (hasNext()) {
            it == next()
        } else {
            false
        }
    }

// ///////////////////////////////////////////////////ASYNCITERATOR/////////////////////////////////////////////////////
public fun <T> Iterator<T>.asyncIterator(): AsyncIterator<T> = IteratorAsyncIterator(this)

private class IteratorAsyncIterator<T>(
    private val iterator: Iterator<T>,
) : AsyncIterator<T> {
    override suspend fun hasNext(): Boolean = iterator.hasNext()

    override suspend fun next(): T = iterator.next()
}

// ////////////////////////////////////////////////////////SOURCE///////////////////////////////////////////////////////
public fun Iterator<Byte>.asSource(): Source = IteratorSource(this)

private class IteratorSource(
    private val iterator: Iterator<Byte>,
) : Source {
    override fun read(
        sink: Buffer,
        byteCount: Long,
    ): Long =
        iterator.next(byteCount.toInt()).let {
            sink.write(it.toByteArray())
            it.size.toLong()
        }

    override fun timeout(): Timeout = Timeout.NONE

    override fun close() = Unit
}

// ////////////////////////////////////////////////////COLLECTION///////////////////////////////////////////////////////
public fun <T> Iterator<T>.toList(destination: MutableList<T> = ArrayList()): List<T> = toCollection(destination)

public fun <T> Iterator<T>.toSet(destination: MutableSet<T> = LinkedHashSet()): Set<T> = toCollection(destination)

public fun <T, C : MutableCollection<in T>> Iterator<T>.toCollection(destination: C): C {
    forEach(destination::add)
    return destination
}
