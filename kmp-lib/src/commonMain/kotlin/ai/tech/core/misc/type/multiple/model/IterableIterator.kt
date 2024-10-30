package ai.tech.core.misc.type.multiple.model

public class IterableIterator<T, I>(
    private val iterable: I,
    private val length: Int,
    private val getter: (I, Int) -> T,
) : AbstractIterator<T>() {
    private var index = 0

    override fun computeNext() {
        if (index < length) {
            setNext(getter(iterable, index++))
        } else {
            done()
        }
    }
}
