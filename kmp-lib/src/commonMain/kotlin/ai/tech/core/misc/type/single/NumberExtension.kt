package ai.tech.core.misc.type.single

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.collections.subtract
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

private val UBYTE_HALF_VALUE: UByte = Byte.MAX_VALUE.toUByte()
private val USHORT_HALF_VALUE: UInt = Int.MAX_VALUE.toUInt()
private val UINT_HALF_VALUE: UInt = Int.MAX_VALUE.toUInt()
private val ULONG_HALF_VALUE: ULong = Long.MAX_VALUE.toULong()

// ////////////////////////////////////////////////////////BYTE/////////////////////////////////////////////////////////
public fun UByte.signed(): Byte = (this - UBYTE_HALF_VALUE - 1u).toByte()

public fun UByte.toIntLSB(): Int = toInt().toLSB()

public fun UByte.toUIntLSB(): UInt = toUInt().toLSB()

public fun UByte.toLongLSB(): Long = toLong().toLSB()

public fun UByte.toULongLSB(): ULong = toULong().toLSB()

// ////////////////////////////////////////////////////////BYTE/////////////////////////////////////////////////////////
public fun Byte.unsigned(): UByte = (this + Byte.MAX_VALUE + 1).toUByte()

public fun Byte.normalize(max: Byte = Byte.MAX_VALUE): Float = toFloat() / max

public fun Byte.toIntLSB(): Int = toInt() and 0xff

public fun Byte.toUIntLSB(): UInt = toUInt() and 0xffu

public fun Byte.toLongLSB(): Long = toLong() and 0xff

public fun Byte.toULongLSB(): ULong = toULong() and 0xffu

// ///////////////////////////////////////////////////////USHORT////////////////////////////////////////////////////////
public fun UShort.signed(): Short = (this - USHORT_HALF_VALUE - 1u).toShort()

// ///////////////////////////////////////////////////////SHORT/////////////////////////////////////////////////////////
public fun Short.unsigned(): UShort = (this + Short.MAX_VALUE + 1).toUShort()

public fun Short.normalize(max: Short = Short.MAX_VALUE): Float = toFloat() / max

// ////////////////////////////////////////////////////////UINT/////////////////////////////////////////////////////////
public fun UInt.signed(): Int = (this - UINT_HALF_VALUE - 1u).toInt()

public fun UInt.toLSB(): UInt = this and 0xffu

public fun UInt.digits(base: UInt): List<UInt> =
    mutableListOf<UInt>().also {
        var n = this
        while (n > 0U) {
            it.add(n % base)
            n /= base
        }
    }

public fun UInt.assignBits(
    lowIndex: Int,
    highIndex: Int,
    set: Boolean,
): UInt =
    intSetBits(lowIndex, highIndex).toUInt().let {
        if (set) {
            this or it
        }
        else {
            this and it.inv()
        }
    }

public fun UInt.sliceBits(
    lowIndex: Int,
    highIndex: Int,
): UInt = (this and (-1 ushr (Int.SIZE_BITS - highIndex - 1)).toUInt()) shr lowIndex

public fun UInt.sliceByte(lowIndex: Int = 0): UInt = sliceBits(lowIndex, lowIndex + 7)

// /////////////////////////////////////////////////////////INT/////////////////////////////////////////////////////////
public fun Int.unsigned(): UInt = (this + Int.MAX_VALUE + 1).toUInt() + 1u

public fun Int.normalize(max: Int = Int.MAX_VALUE): Float = toFloat() / max

public fun Int.toLSB(): Int = this and 0xff

public fun intSetBits(
    lowIndex: Int,
    highIndex: Int,
): Int = (-1 ushr (Int.SIZE_BITS - (highIndex - lowIndex + 1))) shl lowIndex

public fun Int.assignBits(
    lowIndex: Int,
    highIndex: Int,
    set: Boolean,
): Int =
    intSetBits(lowIndex, highIndex).let {
        if (set) {
            this or it
        }
        else {
            this and it.inv()
        }
    }

public fun Int.sliceBits(
    lowIndex: Int,
    highIndex: Int,
): Int = (this and (-1 ushr (Int.SIZE_BITS - highIndex - 1))) ushr lowIndex

public fun Int.sliceByte(lowIndex: Int = 0): Int = sliceBits(lowIndex, lowIndex + 7)

// ///////////////////////////////////////////////////////ULONG/////////////////////////////////////////////////////////
public fun ULong.signed(): Long = (this - ULONG_HALF_VALUE - 1u).toLong()

public fun ULong.toLSB(): ULong = this and 0xffu

public fun ULong.digits(base: ULong = 10UL): List<ULong> =
    mutableListOf<ULong>().also {
        var n = this
        while (n > 0UL) {
            it.add(n % base)
            n /= base
        }
    }

public fun ULong.assignBits(
    lowIndex: Int,
    highIndex: Int,
    set: Boolean,
): ULong =
    longSetBits(lowIndex, highIndex).toULong().let {
        if (set) {
            this or it
        }
        else {
            this and it.inv()
        }
    }

public fun ULong.sliceBits(
    lowIndex: Int,
    highIndex: Int,
): ULong = (this and (-1L ushr (Long.SIZE_BITS - highIndex - 1)).toULong()) shr lowIndex

public fun ULong.sliceByte(lowIndex: Int = 0): ULong = sliceBits(lowIndex, lowIndex + 7)

// ////////////////////////////////////////////////////////LONG/////////////////////////////////////////////////////////
public fun Long.unsigned(): ULong = (this + Long.MAX_VALUE + 1).toULong()

public fun Long.normalize(max: Long = Long.MAX_VALUE): Double = toDouble() / max

public fun Long.toLSB(): Long = this and 0xff

public fun longSetBits(
    lowIndex: Int,
    highIndex: Int,
): Long = (-1L ushr (Int.SIZE_BITS - (highIndex - lowIndex + 1))) shl lowIndex

public fun Long.assignBits(
    lowIndex: Int,
    highIndex: Int,
    set: Boolean,
): Long =
    longSetBits(lowIndex, highIndex).let {
        if (set) {
            this or it
        }
        else {
            this and it.inv()
        }
    }

public fun Long.sliceBits(
    lowIndex: Int,
    highIndex: Int,
): Long = (this and (-1L ushr (Int.SIZE_BITS - highIndex - 1))) ushr lowIndex

public fun Long.sliceByte(lowIndex: Int = 0): Long = sliceBits(lowIndex, lowIndex + 7)

// ////////////////////////////////////////////////////////FLOAT////////////////////////////////////////////////////////
public fun Float.partition(): Pair<Int, Float> =
    "$absoluteValue".split(".").let {
        it[0].toInt() to it[1].toFloat()
    }

public fun Float.denormalizeByte(max: Byte = Byte.MAX_VALUE): Byte = (this * max).toInt().toByte()

public fun Float.denormalizeShort(max: Short = Short.MAX_VALUE): Short = (this * max).toInt().toShort()

public fun Float.denormalizeInt(max: Int = Int.MAX_VALUE): Int = (this * max).toInt()

public fun Float.denormalizeLong(max: Long = Long.MAX_VALUE): Long = (this * max).toLong()

// /////////////////////////////////////////////////////////DOUBLE//////////////////////////////////////////////////////
public fun Double.partition(): Pair<Int, Float> =
    "$absoluteValue".split(".").let {
        it[0].toInt() to ".${it[1]}".toFloat()
    }

public fun Double.denormalizeByte(max: Byte = Byte.MAX_VALUE): Byte = (this * max).toInt().toByte()

public fun Double.denormalizeShort(max: Short = Short.MAX_VALUE): Short = (this * max).toInt().toShort()

public fun Double.denormalizeInt(max: Int = Int.MAX_VALUE): Int = (this * max).toInt()

public fun Double.denormalizeLong(max: Long = Long.MAX_VALUE): Long = (this * max).toLong()

// ////////////////////////////////////////////////////BIGINTEGER///////////////////////////////////////////////////////
public fun BigInteger.normalize(maxValue: BigInteger): BigDecimal =
    BigDecimal.fromBigInteger(this).divide(BigDecimal.fromBigInteger(maxValue))

public fun BigInteger.sliceBits(
    lowIndex: Int,
    highIndex: Int,
): BigInteger = (this and BigInteger.TWO.pow(highIndex)) shr lowIndex

public fun BigInteger.sliceByte(lowIndex: Int = 0): BigInteger = sliceBits(lowIndex, lowIndex + 7)

// ///////////////////////////////////////////////////////ARRAY/////////////////////////////////////////////////////////
public fun UInt.toBitArray(): BooleanArray =
    BooleanArray(UInt.SIZE_BITS) {
        ((this shr it) and 1u) != 0u
    }

public fun UInt.toByteArray(): ByteArray =
    byteArrayOf(
        sliceByte().toByte(),
        sliceByte(8).toByte(),
        sliceByte(16).toByte(),
        sliceByte(24).toByte(),
    )

public fun UInt.toByteArray(size: Int): ByteArray = ByteArray(size) { sliceByte(it * 8).toByte() }

public fun Int.toBitArray(): BooleanArray =
    BooleanArray(Int.SIZE_BITS) {
        ((this ushr it) and 1) != 0
    }

public fun Int.toByteArray(): ByteArray =
    byteArrayOf(
        sliceByte().toByte(),
        sliceByte(8).toByte(),
        sliceByte(16).toByte(),
        sliceByte(24).toByte(),
    )

public fun Int.toByteArray(size: Int): ByteArray = ByteArray(size) { sliceByte(it * 8).toByte() }

public fun ULong.toBitArray(): BooleanArray =
    BooleanArray(ULong.SIZE_BITS) {
        ((this shr it) and 1uL) != 0uL
    }

public fun ULong.toByteArray(): ByteArray =
    byteArrayOf(
        sliceByte().toByte(),
        sliceByte(8).toByte(),
        sliceByte(16).toByte(),
        sliceByte(24).toByte(),
        sliceByte(32).toByte(),
        sliceByte(40).toByte(),
        sliceByte(48).toByte(),
        sliceByte(56).toByte(),
    )

public fun ULong.toByteArray(size: Int): ByteArray = ByteArray(size) { sliceByte(it * 8).toByte() }

public fun Long.toBitArray(): BooleanArray =
    BooleanArray(Long.SIZE_BITS) {
        ((this ushr it) and 1L) != 0L
    }

public fun Long.toByteArray(): ByteArray =
    byteArrayOf(
        sliceByte().toByte(),
        sliceByte(8).toByte(),
        sliceByte(16).toByte(),
        sliceByte(24).toByte(),
        sliceByte(32).toByte(),
        sliceByte(40).toByte(),
        sliceByte(48).toByte(),
        sliceByte(56).toByte(),
    )

public fun Long.toByteArray(size: Int): ByteArray = ByteArray(size) { sliceByte(it * 8).toByte() }

public fun BigInteger.Companion.parseOrNull(s: String): BigInteger? = s.runCatching { parseString(this) }.getOrNull()

public fun BigDecimal.Companion.parseOrNull(s: String): BigDecimal? = s.runCatching { parseString(this) }.getOrNull()

/////////////////////////////////////////////////////NUMBER/////////////////////////////////////////////////////////////
public fun Number.toBigInteger() = BigInteger.parseString(toString())

public fun Any.toBigInteger() = when (this) {
    is BigDecimal -> this
    else -> BigDecimal.parseString(toString())
}

public fun Number.toBigDecimal() = BigDecimal.parseString(toString())

public fun Any.toBigDecimal() = when (this) {
    is BigDecimal -> this
    else -> BigDecimal.parseString(toString())
}

private fun BigDecimal.toNumber(types: List<KClass<*>>) = when {
    types.any { it == BigDecimal::class } -> this
    types.any { it == BigInteger::class } -> toBigInteger()
    types.any { it == Double::class } -> toString().toDouble()
    types.any { it == Float::class } -> toString().toFloat()
    types.any { it == Long::class } -> toString().toLong()
    types.any { it == Int::class } -> toString().toInt()
    types.any { it == Short::class } -> toString().toShort()
    types.any { it == Byte::class } -> toString().toByte()
    types.any { it == ULong::class } -> toString().toLong()
    types.any { it == UInt::class } -> toString().toInt()
    types.any { it == UShort::class } -> toString().toShort()
    types.any { it == UByte::class } -> toString().toByte()
    else -> this
}

private fun List<Any>.operate(block: (BigDecimal, BigDecimal) -> BigDecimal): Any =
    drop(1).fold(get(0).toBigDecimal()) { acc, v -> block(acc, v.toBigDecimal()) }.toNumber(map { it::class })

public fun List<Any>.add(): Any = operate { v1, v2 -> v1.add(v2) }

public fun List<Any>.subtract(): Any = operate { v1, v2 -> v1.subtract(v2) }

public fun List<Any>.multiply(): Any = operate { v1, v2 -> v1.multiply(v2) }

public fun List<Any>.divide(): Any = operate { v1, v2 -> v1.divide(v2) }
