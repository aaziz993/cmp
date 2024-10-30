package ai.tech.core.data.expression

import com.benasher44.uuid.Uuid
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

public val Number.v: NumberVariable
    get() = when (this) {
        is Byte -> v
        is Short -> v
        is Int -> v
        is Long -> v
        is Float -> v
        is Double -> v
        else -> throw IllegalArgumentException("Unknown type \"${this::class.simpleName}\"")
    }

public val <T>  T.v: ComparableOperand
    get() = when (this) {
        is Boolean -> v
        is UByte -> v
        is UShort -> v
        is UInt -> v
        is ULong -> v
        is Number -> v
        is Char -> v
        is String -> v
        is LocalTime -> v
        is LocalDate -> v
        is LocalDateTime -> v
        is BigInteger -> v
        is BigDecimal -> v
        is Uuid -> vl
        else -> this?.let { throw IllegalArgumentException("Unknown expression value type \"${it::class.simpleName}\"") }
            ?: NullValue
    }

@Suppress("UNCHECKED_CAST")
public inline val <reified T> Collection<T>.v: CollectionVariable
    get() = when (T::class) {
        Boolean::class -> (this as Collection<Boolean?>).v
        Byte::class -> (this as Collection<Byte?>).v
        Short::class -> (this as Collection<Short?>).v
        Int::class -> (this as Collection<Int?>).v
        Long::class -> (this as Collection<Long?>).v
        Float::class -> (this as Collection<Float?>).v
        Double::class -> (this as Collection<Double?>).v
        Char::class -> (this as Collection<Char?>).v
        String::class -> (this as Collection<Boolean?>).v
        BigInteger::class -> (this as Collection<BigInteger?>).v
        BigDecimal::class -> (this as Collection<BigDecimal?>).v
        LocalTime::class -> (this as Collection<LocalTime?>).v
        LocalDate::class -> (this as Collection<LocalDate?>).v
        LocalDateTime::class -> (this as Collection<LocalDateTime?>).v
        Uuid::class -> (this as Collection<Uuid?>).v
        else -> throw IllegalArgumentException("Unknown type \"${this::class.simpleName}\"")
    }

public val Boolean.v: BooleanValue
    get() = BooleanValue(this)

public val Collection<Boolean?>.v: BooleanCollection
    get() = BooleanCollection(this)

public val UByte.v: UByteValue
    get() = UByteValue(this)

public val Collection<UByte?>.v: UByteCollection
    get() = UByteCollection(this)

public val Byte.v: ByteValue
    get() = ByteValue(this)

public val Collection<Byte?>.v: ByteCollection
    get() = ByteCollection(this)

public val UShort.v: UShortValue
    get() = UShortValue(this)

public val Collection<UShort?>.v: UShortCollection
    get() = UShortCollection(this)

public val Short.v: ShortValue
    get() = ShortValue(this)

public val Collection<Short?>.v: ShortCollection
    get() = ShortCollection(this)

public val UInt.v: UIntValue
    get() = UIntValue(this)

public val Collection<UInt?>.v: UIntCollection
    get() = UIntCollection(this)

public val Int.v: IntValue
    get() = IntValue(this)

public val Collection<Int?>.v: IntCollection
    get() = IntCollection(this)

public val ULong.v: ULongValue
    get() = ULongValue(this)

public val Collection<ULong?>.v: ULongCollection
    get() = ULongCollection(this)

public val Long.v: LongValue
    get() = LongValue(this)

public val Collection<Long?>.v: LongCollection
    get() = LongCollection(this)

public val Float.v: FloatValue
    get() = FloatValue(this)

public val Collection<Float?>.v: FloatCollection
    get() = FloatCollection(this)

public val Double.v: DoubleValue
    get() = DoubleValue(this)

public val Collection<Double?>.v: DoubleCollection
    get() = DoubleCollection(this)

public val Char.v: CharValue
    get() = CharValue(this)

public val Collection<Char?>.v: CharCollection
    get() = CharCollection(this)

public val String.v: StringValue
    get() = StringValue(this)

public val Collection<String?>.v: StringCollection
    get() = StringCollection(this)

public val BigInteger.v: BigIntegerValue
    get() = BigIntegerValue(this)

public val Collection<BigInteger?>.v: BigIntegerCollection
    get() = BigIntegerCollection(this)

public val BigDecimal.v: BigDecimalValue
    get() = BigDecimalValue(this)

public val Collection<BigDecimal?>.v: BigDecimalCollection
    get() = BigDecimalCollection(this)

public val LocalTime.v: LocalTimeValue
    get() = LocalTimeValue(this)

public val Collection<LocalTime?>.v: LocalTimeCollection
    get() = LocalTimeCollection(this)

public val LocalDate.v: LocalDateValue
    get() = LocalDateValue(this)

public val Collection<LocalDate?>.v: LocalDateCollection
    get() = LocalDateCollection(this)

public val LocalDateTime.v: LocalDateTimeValue
    get() = LocalDateTimeValue(this)

public val Collection<LocalDateTime?>.v: LocalDateTimeCollection
    get() = LocalDateTimeCollection(this)

public val Uuid.vl: UUIDValue
    get() = UUIDValue(this)

public val Collection<Uuid?>.v: UUIDCollection
    get() = UUIDCollection(this)

public val String.f: Field
    get() = Field(this)

public fun String.p(alias: String? = null, distinct: Boolean = false): Projection = Projection(this, alias, distinct)

public infix fun <T : Comparable<T>> T.eqExp(other: T): Equals = v.eq(other)

public infix fun <T : Comparable<T>> T.neqExp(other: T): NotEquals = v.neq(other)

public infix fun <T : Comparable<T>> T.gtExp(other: T): GreaterThan = v.gt(other)

public infix fun <T : Comparable<T>> T.gteExp(other: T): GreaterEqualThan = v.gte(other)

public infix fun <T : Comparable<T>> T.ltExp(other: T): LessThan = v.lt(other)

public infix fun <T : Comparable<T>> T.lteExp(other: T): LessEqualThan = v.lte(other)

public fun <T : Comparable<T>> T.betweenExp(left: T, right: T): Between = v.between(left, right)

public inline infix fun <reified T : Comparable<T>> T.inExp(collection: Collection<T>): In = v.`in`(collection)

public inline infix fun <reified T : Comparable<T>> T.ninExp(collection: Collection<T>): NotIn = v.nin(collection)

public fun Boolean.andExp(vararg values: Boolean): And = v.and(*values)

public fun Boolean.orExp(vararg values: Boolean): Or = v.or(*values)

public fun Boolean.xorExp(vararg values: Boolean): XOr = v.xor(*values)

public val Boolean.notExp: Not
    get() = v.not

public fun Number.addExp(vararg values: Number): Add = v.add(*values)

public fun Number.subtractExp(vararg values: Number): Subtract = v.subtract(*values)

public fun Number.multiplyExp(vararg values: Number): Multiply = v.multiply(*values)

public fun Number.divideExp(vararg values: Number): Divide = v.divide(*values)

public infix fun Number.modExp(divisor: Number): Mod = v.mod(divisor)

public infix fun Number.powExp(power: Number): Pow = v.pow(power)

public val Number.squareExp: Square
    get() = v.square

public fun String.eqExp(other: String, matchAll: Boolean, ignoreCase: Boolean): Equals =
    v.eq(other, matchAll, ignoreCase)

public fun String.eqPatternExp(pattern: String, matchAll: Boolean, ignoreCase: Boolean): EqualsPattern =
    v.eqPattern(pattern, matchAll, ignoreCase)

public val String.asciiExp: Ascii
    get() = v.ascii

public val String.lenExp: Length
    get() = v.len

public val String.lowerExp: Lower
    get() = v.lower

public val String.upperExp: Upper
    get() = v.upper

public fun String.substrExp(startIndex: Int, endIndex: Int? = null): SubString = v.substr(startIndex, endIndex)

public fun String.replaceExp(value: String, replacement: String, ignoreCase: Boolean): Replace =
    v.replace(value, replacement, ignoreCase)

public fun String.replacePatternExp(pattern: String, replacement: String, ignoreCase: Boolean): ReplacePattern =
    v.replacePattern(pattern, replacement, ignoreCase)

public val String.reverseExp: Reverse
    get() = v.reverse

public fun String.trimExp(vararg chars: Char): Trim = v.trim(*chars)

public fun String.trimStartExp(vararg chars: Char): TrimStart = v.trimStart(*chars)

public fun String.trimEndExp(vararg chars: Char): TrimEnd = v.trimEnd(*chars)

public fun String.padStartExp(length: Int, vararg chars: Char): PadStart = v.padStart(length, *chars)

public fun String.padEndExp(length: Int, vararg chars: Char): PadEnd = v.padEnd(length, *chars)

public infix fun String.leftExp(length: Int): Left = v.left(length)

public infix fun String.rightExp(length: Int): Right = v.right(length)

public infix fun String.replicateExp(length: Int): Replicate = v.replicate(length)

public fun String.indexOfExp(pattern: String, ignoreCase: Boolean): IndexOf = v.indexOf(pattern, ignoreCase)

public fun String.indexOfPatternExp(pattern: String, ignoreCase: Boolean): IndexOfPattern =
    v.indexOfPattern(pattern, ignoreCase)

public fun String.splitExp(separator: String, ignoreCase: Boolean): Split = v.split(separator, ignoreCase)

public fun String.splitPatternExp(separator: String, ignoreCase: Boolean): SplitPattern =
    v.splitPattern(separator, ignoreCase)

public val LocalTime.timeExp: Time
    get() = v.time

public val LocalDate.dateExp: Date
    get() = v.date

public val LocalDateTime.dateTimeExp: DateTime
    get() = v.dateTime

public infix fun LocalDateTime.formatExp(format: String): TemporalFormat = v.format(format)


