package ai.tech.core.misc.type.multiple

import ai.tech.core.data.model.ImageCompression
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.convertTo
import com.github.ajalt.colormath.model.Ansi16
import com.github.ajalt.colormath.model.Ansi256
import com.github.ajalt.colormath.model.RGB
import ai.tech.core.data.model.Charset
import ai.tech.core.misc.type.single.normalize
import ai.tech.core.misc.type.single.toIntLSB
import ai.tech.core.misc.type.single.toLongLSB
import ai.tech.core.misc.type.single.toUIntLSB
import ai.tech.core.misc.type.single.toULongLSB
import ai.tech.core.misc.type.single.unsigned
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

public inline fun <reified T> merge(arrays: List<Array<T>>): Array<T> =
    Array(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public inline fun <reified T> Array<T>.unmerge(step: Int): List<Array<T>> =
    List(step) { offset ->
        Array(size / step) {
            this[offset + it * step]
        }
    }

public fun merge(arrays: List<BooleanArray>): BooleanArray =
    BooleanArray(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public fun BooleanArray.unmerge(step: Int): List<BooleanArray> =
    List(step) { offset ->
        BooleanArray(size / step) {
            this[offset + it * step]
        }
    }

public inline fun merge(arrays: List<ByteArray>): ByteArray =
    ByteArray(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public fun ByteArray.unmerge(step: Int): List<ByteArray> =
    List(step) { offset ->
        ByteArray(size / step) {
            this[offset + it * step]
        }
    }

public inline fun merge(arrays: List<ShortArray>): ShortArray =
    ShortArray(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public inline fun ShortArray.unmerge(step: Int): List<ShortArray> =
    List(step) { offset ->
        ShortArray(size / step) {
            this[offset + it * step]
        }
    }

public inline fun merge(arrays: List<IntArray>): IntArray =
    IntArray(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public inline fun IntArray.unmerge(step: Int): List<IntArray> =
    List(step) { offset ->
        IntArray(size / step) {
            this[offset + it * step]
        }
    }

public inline fun merge(arrays: List<LongArray>): LongArray =
    LongArray(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public inline fun LongArray.unmerge(step: Int): List<LongArray> =
    List(step) { offset ->
        LongArray(size / step) {
            this[offset + it * step]
        }
    }

public inline fun merge(arrays: List<FloatArray>): FloatArray =
    FloatArray(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public inline fun FloatArray.unmerge(step: Int): List<FloatArray> =
    List(step) { offset ->
        FloatArray(size / step) {
            this[offset + it * step]
        }
    }

public inline fun merge(arrays: List<DoubleArray>): DoubleArray =
    DoubleArray(arrays.size * arrays[0].size) {
        arrays[it % arrays.size][it / arrays.size]
    }

public inline fun DoubleArray.unmerge(step: Int): List<DoubleArray> =
    List(step) { offset ->
        DoubleArray(size / step) {
            this[offset + it * step]
        }
    }

// ////////////////////////////////////////////////////////COLOR////////////////////////////////////////////////////////
public fun <T : Color> ByteArray.toColor(
    colorSpace: ColorSpace<T>,
    offset: Int = 0,
    alpha: Boolean = true,
): T =
    when (colorSpace) {
        RGB ->
            colorSpace.create(
                (
                    copyOfRange(offset, offset + 3)
                        .map { it.normalize() }
                        .toMutableList() +
                        (if (alpha) this[offset + 3].normalize() else 1f)
                    ).toFloatArray(),
            )

        Ansi16, Ansi256 ->
            colorSpace.create(
                (
                    mutableListOf(
                        this[offset].unsigned().toFloat(),
                    ) + (if (alpha) this[offset + 1].normalize() else 1f)
                    ).toFloatArray(),
            )

        else -> {
            val size = (colorSpace.components.size - 1) * 4
            colorSpace.create(
                copyOfRange(offset, offset + size)
                    .let { array ->
                        (
                            (array.indices step 4)
                                .map { array.toInt(it).normalize() }
                                .toMutableList() +
                                (if (alpha) this[offset + size].normalize() else 1f)
                            ).toFloatArray()
                    },
            )
        }
    }

private fun ColorSpace<*>.size(alpha: Boolean) =
    (if (alpha) 1 else 0) +
        when (this) {
            RGB, Ansi16, Ansi256 -> components.size - 1
            else -> (components.size - 1) * 4
        }

public fun <T : Color> ByteArray.toColor(
    toColorSpace: ColorSpace<T>,
    fromColorSpace: ColorSpace<*> = RGB,
    alpha: Boolean = true,
): ByteArray =
    (indices step fromColorSpace.size(alpha)).fold(byteArrayOf()) { acc, v ->
        acc +
            toColor(
                fromColorSpace,
                v,
                alpha,
            ).convertTo(toColorSpace).toByteArray()
    }

// //////////////////////////////////////////////////////IMAGE//////////////////////////////////////////////////////////
public expect suspend fun ByteArray.compressRGBA(
    width: Number,
    height: Number,
    compression: ImageCompression,
): ByteArray

public expect suspend fun ByteArray.decompressRGBA(compression: ImageCompression): ByteArray

// //////////////////////////////////////////////////////UINT///////////////////////////////////////////////////////////
public fun BooleanArray.toUInt(): UInt = map { if (it) 1u else 0u }.foldIndexed(0u) { i, acc, v -> acc or (v shl i) }

public fun ByteArray.toUInt(offset: Int = 0): UInt =
    this[offset].toUIntLSB() or
        (this[offset + 1].toUIntLSB() shl 8) or
        (this[offset + 2].toUIntLSB() shl 16) or
        (this[offset + 3].toUIntLSB() shl 24)

public fun ByteArray.toUInt(
    size: Int,
    offset: Int = 0,
): UInt {
    var value = 0u
    (0 until size).forEach { value = value or (this[offset + it].toUIntLSB() shl (it * 8)) }
    return value
}

// ///////////////////////////////////////////////////////INT///////////////////////////////////////////////////////////
public fun BooleanArray.toInt(): Int = map { if (it) 1 else 0 }.foldIndexed(0) { i, acc, v -> acc or (v shl i) }

public fun ByteArray.toInt(offset: Int = 0): Int =
    this[offset].toIntLSB() or
        (this[offset + 1].toIntLSB() shl 8) or
        (this[offset + 2].toIntLSB() shl 16) or
        (this[offset + 3].toIntLSB() shl 24)

public fun ByteArray.toInt(
    size: Int,
    offset: Int = 0,
): Int {
    var value = 0
    (0 until size).forEach { value = value or (this[offset + it].toIntLSB() shl (it * 8)) }
    return value
}

// //////////////////////////////////////////////////////ULONG//////////////////////////////////////////////////////////
public fun BooleanArray.toULong(): ULong = map { if (it) 1uL else 0uL }.foldIndexed(0uL) { i, acc, v -> acc or (v shl i) }

public fun ByteArray.toULong(
    offset: Int =
        0,
): ULong =
    this[offset].toULongLSB() or
        (this[offset + 1].toULongLSB() shl 8) or
        (this[offset + 2].toULongLSB() shl 16) or
        (this[offset + 3].toULongLSB() shl 24) or
        (this[offset + 4].toULongLSB() shl 32) or
        (this[offset + 5].toULongLSB() shl 40) or
        (this[offset + 6].toULongLSB() shl 48) or
        (this[offset + 7].toULongLSB() shl 56)

public fun ByteArray.toULong(
    size: Int,
    offset: Int = 0,
): ULong {
    var value = 0uL
    (0 until size).forEach { value = value or (this[offset + it].toULongLSB() shl (it * 8)) }
    return value
}

// ///////////////////////////////////////////////////////LONG//////////////////////////////////////////////////////////
public fun BooleanArray.toLong(): Long = map { if (it) 1L else 0L }.foldIndexed(0L) { i, acc, v -> acc or (v shl i) }

public fun ByteArray.toLong(
    offset: Int =
        0,
): Long =
    this[offset].toLongLSB() or
        (this[offset + 1].toLongLSB() shl 8) or
        (this[offset + 2].toLongLSB() shl 16) or
        (this[offset + 3].toLongLSB() shl 24) or
        (this[offset + 4].toLongLSB() shl 32) or
        (this[offset + 5].toLongLSB() shl 40) or
        (this[offset + 6].toLongLSB() shl 48) or
        (this[offset + 7].toLongLSB() shl 56)

public fun ByteArray.toLong(
    size: Int,
    offset: Int = 0,
): Long {
    var value = 0L
    (0 until size).forEach { value = value or (this[offset + it].toLongLSB() shl (it * 8)) }
    return value
}

// /////////////////////////////////////////////////////BASE64//////////////////////////////////////////////////////////
@OptIn(ExperimentalEncodingApi::class)
public fun ByteArray.encodeBase64(startIndex: Int = 0, endIndex: Int = size): String = Base64.encode(this, startIndex, endIndex)

@OptIn(ExperimentalEncodingApi::class)
public fun String.decodeBase64(startIndex: Int = 0, endIndex: Int = length): ByteArray = Base64.decode(this, startIndex, endIndex)

// /////////////////////////////////////////////////////STRING//////////////////////////////////////////////////////////
public fun ByteArray.decode(charset: Charset = Charset.UTF_8): String = decodeToString(Charsets.forName(charset.name))
