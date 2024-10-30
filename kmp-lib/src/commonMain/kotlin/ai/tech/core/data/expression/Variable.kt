package ai.tech.core.data.expression

import com.benasher44.uuid.Uuid
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import ai.tech.core.misc.serializer.BigDecimalSerial
import ai.tech.core.misc.core.serializer.BigIntegerSerial
import ai.tech.core.misc.core.serializer.UuidSerial
import ai.tech.core.misc.core.type.multiple.removeLast
import ai.tech.core.misc.core.type.transform
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public sealed class Variable

public interface ComparableOperand {
    public infix fun eq(other: ComparableOperand): Equals = CompareExpression.eq(this, other)

    public infix fun <T> eq(other: T): Equals = eq(other.v)

    public infix fun neq(other: ComparableOperand): NotEquals = CompareExpression.neq(this, other)

    public infix fun <T> neq(other: T): NotEquals = neq(other.v)

    public infix fun gt(other: ComparableOperand): GreaterThan = CompareExpression.gt(this, other)

    public infix fun <T> gt(other: T): GreaterThan = gt(other.v)

    public infix fun gte(other: ComparableOperand): GreaterEqualThan = CompareExpression.gte(this, other)

    public infix fun <T> gte(other: T): GreaterEqualThan = gte(other.v)

    public infix fun lt(other: ComparableOperand): LessThan = CompareExpression.lt(this, other)

    public infix fun <T> lt(other: T): LessThan = lt(other.v)

    public infix fun lte(other: ComparableOperand): LessEqualThan = CompareExpression.lte(this, other)

    public infix fun <T> lte(other: T): LessEqualThan = lte(other.v)

    public fun between(left: ComparableOperand, right: ComparableOperand): Between =
        CompareExpression.between(this, left, right)

    public fun <T> between(left: T, right: T): Between = between(left.v, right.v)

    public infix fun `in`(other: CollectionVariable): In = CompareExpression.`in`(this, other)

    public infix fun nin(other: CollectionVariable): NotIn = CompareExpression.nin(this, other)
}

public inline infix fun <reified T> ComparableOperand.`in`(other: Collection<T>): In = `in`(other.v)

public inline infix fun <reified T> ComparableOperand.nin(other: Collection<T>): NotIn = nin(other.v)

@Serializable
public sealed class ComparableVariable : Variable(), ComparableOperand

public interface BooleanOperand {
    public fun and(vararg values: BooleanOperand): And = LogicExpression.and(this, *values)

    public fun and(vararg values: Boolean): And = and(*values.map { it.v }.toTypedArray())

    public fun or(vararg values: BooleanOperand): Or = LogicExpression.or(this, *values)

    public fun or(vararg values: Boolean): Or = or(*values.map { it.v }.toTypedArray())

    public fun xor(vararg values: BooleanOperand): XOr = LogicExpression.xor(this, *values)

    public fun xor(vararg values: Boolean): XOr = xor(*values.map { it.v }.toTypedArray())

    public val not: Not
        get() = LogicExpression.not(this)
}

@Serializable
public sealed class BooleanVariable : ComparableVariable(), BooleanOperand

public interface NumberOperand {
    public fun add(vararg values: NumberOperand): Add = ArithmeticExpression.add(this, *values)

    public fun add(vararg values: Number): Add = add(*values.map { it.v }.toTypedArray())

    public fun subtract(vararg values: NumberOperand): Subtract = ArithmeticExpression.subtract(this, *values)

    public fun subtract(vararg values: Number): Subtract = subtract(*values.map { it.v }.toTypedArray())

    public fun multiply(vararg values: NumberOperand): Multiply = ArithmeticExpression.multiply(this, *values)

    public fun multiply(vararg values: Number): Multiply = multiply(*values.map { it.v }.toTypedArray())

    public fun divide(vararg values: NumberOperand): Divide = ArithmeticExpression.divide(this, *values)

    public fun divide(vararg values: Number): Divide = divide(*values.map { it.v }.toTypedArray())

    public infix fun mod(divisor: NumberOperand): Mod = ArithmeticExpression.mod(this, divisor)

    public infix fun mod(divisor: Number): Mod = mod(divisor.v)

    public infix fun pow(power: NumberOperand): Pow = ArithmeticExpression.pow(this, power)

    public infix fun pow(power: Number): Pow = pow(power.v)

    public val square: Square
        get() = ArithmeticExpression.square(this)
}

@Serializable
public sealed class NumberVariable : ComparableVariable(), NumberOperand

@Serializable
public sealed class CharVariable : ComparableVariable()

public interface StringOperand {
    public fun eq(other: StringOperand, matchAll: BooleanOperand, ignoreCase: BooleanOperand): Equals =
        StringExpression.eq(this, other, matchAll, ignoreCase)

    public fun eq(other: String, matchAll: Boolean, ignoreCase: Boolean): Equals =
        eq(other.v, matchAll.v, ignoreCase.v)

    public fun eqPattern(
        pattern: StringOperand,
        matchAll: BooleanOperand,
        ignoreCase: BooleanOperand
    ): EqualsPattern =
        StringExpression.eqPattern(this, pattern, matchAll, ignoreCase)

    public fun eqPattern(pattern: String, matchAll: Boolean, ignoreCase: Boolean): EqualsPattern =
        eqPattern(pattern.v, matchAll.v, ignoreCase.v)

    public val ascii: Ascii
        get() = StringExpression.ascii(this)

    public val len: Length get() = StringExpression.len(this)

    public val lower: Lower get() = StringExpression.lower(this)

    public val upper: Upper get() = StringExpression.upper(this)

    public fun substr(
        startIndex: NumberOperand,
        endIndex: NumberOperand? = null
    ): SubString = StringExpression.substr(this, startIndex, endIndex)

    public fun substr(
        startIndex: Int,
        endIndex: Int? = null
    ): SubString = substr(startIndex.v, endIndex?.v)

    public fun replace(variable: StringOperand, replacement: StringOperand, ignoreCase: BooleanOperand): Replace =
        StringExpression.replace(this, variable, replacement, ignoreCase)

    public fun replace(value: String, replacement: String, ignoreCase: Boolean): Replace =
        replace(value.v, replacement.v, ignoreCase.v)

    public fun replacePattern(
        pattern: StringOperand,
        replacement: StringOperand,
        ignoreCase: BooleanOperand
    ): ReplacePattern =
        StringExpression.replacePattern(this, pattern, replacement, ignoreCase)

    public fun replacePattern(pattern: String, replacement: String, ignoreCase: Boolean): ReplacePattern =
        replacePattern(pattern.v, replacement.v, ignoreCase.v)

    public val reverse: Reverse
        get() = StringExpression.reverse(this)

    public fun trim(vararg charVariables: CharVariable): Trim =
        StringExpression.trim(this, *charVariables)

    public fun trim(vararg chars: Char): Trim = trim(*chars.map { it.v }.toTypedArray())

    public fun trimStart(vararg charVariables: CharVariable): TrimStart =
        StringExpression.trimStart(this, *charVariables)

    public fun trimStart(vararg chars: Char): TrimStart = trimStart(*chars.map { it.v }.toTypedArray())

    public fun trimEnd(vararg charVariables: CharVariable): TrimEnd =
        StringExpression.trimEnd(this, *charVariables)

    public fun trimEnd(vararg chars: Char): TrimEnd = trimEnd(*chars.map { it.v }.toTypedArray())

    public fun padStart(length: NumberOperand, vararg charVariables: CharVariable): PadStart =
        StringExpression.padStart(this, length, *charVariables)

    public fun padStart(length: Int, vararg chars: Char): PadStart =
        padStart(length.v, *chars.map { it.v }.toTypedArray())

    public fun padEnd(length: NumberOperand, vararg charVariables: CharVariable): PadEnd =
        StringExpression.padEnd(this, length, *charVariables)

    public fun padEnd(length: Int, vararg chars: Char): PadEnd =
        padEnd(length.v, *chars.map { it.v }.toTypedArray())

    public infix fun left(length: NumberOperand): Left = StringExpression.left(this, length)

    public infix fun left(length: Int): Left = left(length.v)

    public infix fun right(length: NumberOperand): Right = StringExpression.right(this, length)

    public infix fun right(length: Int): Right = right(length.v)

    public infix fun replicate(length: NumberOperand): Replicate = StringExpression.replicate(this, length)

    public infix fun replicate(length: Int): Replicate = replicate(length.v)

    public fun indexOf(variable: StringOperand, ignoreCase: BooleanOperand): IndexOf =
        StringExpression.indexOf(this, variable, ignoreCase)

    public fun indexOf(value: String, ignoreCase: Boolean): IndexOf = indexOf(value.v, ignoreCase.v)

    public fun indexOfPattern(pattern: StringOperand, ignoreCase: BooleanOperand): IndexOfPattern =
        StringExpression.indexOfPattern(this, pattern, ignoreCase)

    public fun indexOfPattern(pattern: String, ignoreCase: Boolean): IndexOfPattern =
        indexOfPattern(pattern.v, ignoreCase.v)

    public fun split(separator: StringOperand, ignoreCase: BooleanOperand): Split =
        StringExpression.split(this, separator, ignoreCase)

    public fun split(separator: String, ignoreCase: Boolean): Split = split(separator.v, ignoreCase.v)

    public fun splitPattern(separator: StringOperand, ignoreCase: BooleanOperand): SplitPattern =
        StringExpression.splitPattern(this, separator, ignoreCase)

    public fun splitPattern(separator: String, ignoreCase: Boolean): SplitPattern =
        splitPattern(separator.v, ignoreCase.v)
}

@Serializable
public sealed class StringVariable : ComparableVariable(), StringOperand

public interface TemporalOperand {
    public val time: Time
        get() = TemporalExpression.time(this)

    public val date: Date
        get() = TemporalExpression.date(this)

    public val dateTime: DateTime
        get() = TemporalExpression.dateTime(this)

    public infix fun format(format: StringOperand): TemporalFormat = TemporalExpression.format(this, format)

    public infix fun format(format: String): TemporalFormat = TemporalExpression.format(this, format.v)
}

@Serializable
public sealed class TemporalVariable : ComparableVariable(), TemporalOperand

@Serializable
public sealed class CollectionVariable : Variable()

public interface Expression {
    public val arguments: List<Variable>

    public val isSimple: Boolean
        get() = arguments.all { it is Value<*> }

    public fun evaluate(
        childExpression: Expression.(expressions: List<Expression>) -> Unit,
        parentExpression: Expression.(expressions: List<Expression>) -> Unit
    ): Unit =
        transform({ transforms, value ->
            if (value is Expression) {
                if (value.isSimple) {
                    value.childExpression(transforms)
                } else {
                    return@transform value.arguments.iterator()
                }
            }
            null
        }) { transforms, transform ->
            (transform as Expression).parentExpression(transforms)
        }

    public fun evaluate(
        childExpression: Expression.() -> Any?,
        parentExpression: Expression.(args: List<Any?>) -> Any?
    ): Any? {
        val values = mutableListOf<Any?>()

        transform({ _, value ->
            if (value is Expression) {
                if (value.isSimple) {
                    values.add(value.childExpression())
                } else {
                    return@transform value.arguments.iterator()
                }
            }
            null
        }) { _, transform ->
            values.add((transform as Expression).parentExpression(values.removeLast((transform.arguments.size))))
        }

        return values[0]
    }
}


@Suppress("UNCHECKED_CAST")
@Serializable
public sealed class CompareExpression : BooleanVariable(), Expression {
    public companion object {
        public fun eq(left: ComparableOperand, right: ComparableOperand): Equals =
            Equals(listOf(left, right) as List<Variable>)

        public fun neq(left: ComparableOperand, right: ComparableOperand): NotEquals =
            NotEquals(listOf(left, right) as List<Variable>)

        public fun gt(left: ComparableOperand, right: ComparableOperand): GreaterThan =
            GreaterThan(listOf(left, right) as List<Variable>)

        public fun gte(left: ComparableOperand, right: ComparableOperand): GreaterEqualThan =
            GreaterEqualThan(listOf(left, right) as List<Variable>)

        public fun lt(left: ComparableOperand, right: ComparableOperand): LessThan =
            LessThan(listOf(left, right) as List<Variable>)

        public fun lte(left: ComparableOperand, right: ComparableOperand): LessEqualThan =
            LessEqualThan(listOf(left, right) as List<Variable>)

        public fun between(variable: ComparableOperand, left: ComparableOperand, right: ComparableOperand): Between =
            Between(listOf(variable, left, right) as List<Variable>)

        public fun `in`(variable: ComparableOperand, collection: CollectionVariable): In =
            In(listOf(variable, collection) as List<Variable>)

        public fun nin(variable: ComparableOperand, collection: CollectionVariable): NotIn =
            NotIn(listOf(variable, collection) as List<Variable>)
    }
}

@Suppress("UNCHECKED_CAST")
@Serializable
public sealed class LogicExpression : BooleanVariable(), Expression {
    public companion object {
        public fun and(vararg values: BooleanOperand): And = And(values.toList() as List<Variable>)

        public fun or(vararg values: BooleanOperand): Or = Or(values.toList() as List<Variable>)

        public fun xor(vararg values: BooleanOperand): XOr = XOr(values.toList() as List<Variable>)

        public fun not(value: BooleanOperand): Not = Not(listOf(value) as List<Variable>)
    }
}

@Serializable
public data class And(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class Or(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class XOr(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class Not(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class Equals(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class NotEquals(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class GreaterThan(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class GreaterEqualThan(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class LessThan(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class LessEqualThan(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class Between(override val arguments: List<Variable>) :
    LogicExpression()

@Serializable
public data class In(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class NotIn(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public sealed class AggregateExpression<T> {
    public companion object {
        public fun count(projection: Projection? = null): Count = Count(projection)

        public fun <T> max(projection: Projection): Max<T> = Max(projection)

        public fun <T> min(projection: Projection): Min<T> = Min(projection)

        public fun <T> avg(projection: Projection): Avg<T> = Avg(projection)

        public fun <T> sum(projection: Projection): Sum<T> = Sum(projection)
    }
}

@Serializable
public data class Count(val projection: Projection?) : AggregateExpression<Long>()

@Serializable
public data class Max<T>(val projection: Projection) : AggregateExpression<T>()

@Serializable
public data class Min<T>(val projection: Projection) : AggregateExpression<T>()

@Serializable
public data class Avg<T>(val projection: Projection) : AggregateExpression<T>()

@Serializable
public data class Sum<T>(val projection: Projection) : AggregateExpression<T>()

@Suppress("UNCHECKED_CAST")
@Serializable
public sealed class ArithmeticExpression : NumberVariable(), Expression {
    public companion object {
        public fun add(vararg values: NumberOperand): Add = Add(values.toList() as List<Variable>)

        public fun subtract(vararg values: NumberOperand): Subtract = Subtract(values.toList() as List<Variable>)

        public fun multiply(vararg values: NumberOperand): Multiply = Multiply(values.toList() as List<Variable>)

        public fun divide(vararg values: NumberOperand): Divide = Divide(values.toList() as List<Variable>)

        public fun mod(divident: NumberOperand, divisor: NumberOperand): Mod =
            Mod(listOf(divident, divisor) as List<Variable>)

        public fun pow(variable: NumberOperand, power: NumberOperand): Pow =
            Pow(listOf(variable, power) as List<Variable>)

        public fun square(variable: NumberOperand): Square = Square(listOf(variable) as List<Variable>)
    }
}

@Serializable
public data class Add(override val arguments: List<Variable>) : ArithmeticExpression()

@Serializable
public data class Subtract(override val arguments: List<Variable>) : ArithmeticExpression()

@Serializable
public data class Multiply(override val arguments: List<Variable>) : ArithmeticExpression()

@Serializable
public data class Divide(override val arguments: List<Variable>) : ArithmeticExpression()

@Serializable
public data class Mod(override val arguments: List<Variable>) : ArithmeticExpression()

@Serializable
public data class Pow(override val arguments: List<Variable>) : ArithmeticExpression()

@Serializable
public data class Square(override val arguments: List<Variable>) : ArithmeticExpression()

@Suppress("UNCHECKED_CAST")
@Serializable
public sealed class TemporalExpression : TemporalVariable(), Expression {
    public companion object {
        public val now: Now = Now

        public fun time(variable: TemporalOperand): Time = Time(listOf(variable) as List<Variable>)

        public fun date(variable: TemporalOperand): Date = Date(listOf(variable) as List<Variable>)

        public fun dateTime(variable: TemporalOperand): DateTime = DateTime(listOf(variable) as List<Variable>)

        public fun dateTimeOffset(variable: TemporalOperand): DateTimeOffset =
            DateTimeOffset(listOf(variable) as List<Variable>)

        public fun format(variable: TemporalOperand, format: StringOperand): TemporalFormat =
            TemporalFormat(listOf(variable, format) as List<Variable>)
    }
}

@Serializable
public data object Now : TemporalExpression() {
    override val arguments: List<Variable> = emptyList()
}

@Serializable
public data class Time(override val arguments: List<Variable>) : TemporalExpression()

@Serializable
public data class Date(override val arguments: List<Variable>) : TemporalExpression()

@Serializable
public data class DateTime(override val arguments: List<Variable>) : TemporalExpression()

@Serializable
public data class DateTimeOffset(override val arguments: List<Variable>) : TemporalExpression()

@Serializable
public data class TemporalFormat(override val arguments: List<Variable>) :
    TemporalExpression()

@Suppress("UNCHECKED_CAST")
@Serializable
public sealed class StringExpression : StringVariable(), Expression {
    public companion object {
        public fun eq(
            left: StringOperand,
            right: StringOperand,
            matchAll: BooleanOperand,
            ignoreCase: BooleanOperand
        ): Equals =
            Equals(listOf(left, right, matchAll, ignoreCase) as List<Variable>)

        public fun eqPattern(
            left: StringOperand,
            right: StringOperand,
            matchAll: BooleanOperand,
            ignoreCase: BooleanOperand
        ): EqualsPattern =
            EqualsPattern(listOf(left, right, matchAll, ignoreCase) as List<Variable>)

        public fun ascii(variable: StringOperand): Ascii = Ascii(listOf(variable) as List<Variable>)

        public fun len(variable: StringOperand): Length = Length(listOf(variable) as List<Variable>)

        public fun lower(variable: StringOperand): Lower = Lower(listOf(variable) as List<Variable>)

        public fun upper(variable: StringOperand): Upper = Upper(listOf(variable) as List<Variable>)

        public fun substr(
            variable: StringOperand,
            startIndex: NumberOperand,
            endIndex: NumberOperand? = null
        ): SubString = SubString(listOfNotNull(variable, startIndex, endIndex) as List<Variable>)

        public fun replace(
            variable: StringOperand,
            pattern: StringOperand,
            replacement: StringOperand,
            ignoreCase: BooleanOperand,
        ): Replace =
            Replace(listOf(variable, pattern, replacement, ignoreCase) as List<Variable>)

        public fun replacePattern(
            variable: StringOperand,
            pattern: StringOperand,
            replacement: StringOperand,
            ignoreCase: BooleanOperand,
        ): ReplacePattern =
            ReplacePattern(listOf(variable, pattern, replacement, ignoreCase) as List<Variable>)

        public fun reverse(variable: StringOperand): Reverse = Reverse(listOf(variable) as List<Variable>)

        public fun trim(variable: StringOperand, vararg trimValues: CharVariable): Trim =
            Trim((listOf(variable) + trimValues.toList()) as List<Variable>)

        public fun trimStart(variable: StringOperand, vararg trimValues: CharVariable): TrimStart =
            TrimStart((listOf(variable) + trimValues.toList()) as List<Variable>)


        public fun trimEnd(variable: StringOperand, vararg trimValues: CharVariable): TrimEnd =
            TrimEnd((listOf(variable) + trimValues.toList()) as List<Variable>)

        public fun padStart(variable: StringOperand, count: NumberOperand, vararg padValues: CharVariable): PadStart =
            PadStart((listOf(variable, count) + padValues.toList()) as List<Variable>)

        public fun padEnd(variable: StringOperand, count: NumberOperand, vararg padValues: CharVariable): PadEnd =
            PadEnd((listOf(variable, count) + padValues.toList()) as List<Variable>)

        public fun left(variable: StringOperand, count: NumberOperand): Left =
            Left(listOf(variable, count) as List<Variable>)

        public fun right(variable: StringOperand, count: NumberOperand): Right =
            Right(listOf(variable, count) as List<Variable>)

        public fun replicate(variable: StringOperand, count: NumberOperand): Replicate =
            Replicate(listOf(variable, count) as List<Variable>)

        public fun indexOf(variable: StringOperand, pattern: StringOperand, ignoreCase: BooleanOperand): IndexOf =
            IndexOf(listOf(variable, pattern, ignoreCase) as List<Variable>)

        public fun indexOfPattern(
            variable: StringOperand,
            pattern: StringOperand,
            ignoreCase: BooleanOperand
        ): IndexOfPattern =
            IndexOfPattern(listOf(variable, pattern, ignoreCase) as List<Variable>)

        public fun space(length: NumberOperand): Space = Space(listOf(length) as List<Variable>)

        public fun space(length: Int): Space = space(length.v)

        public fun split(variable: StringOperand, separator: StringOperand, ignoreCase: BooleanOperand): Split =
            Split(listOf(variable, separator, ignoreCase) as List<Variable>)

        public fun splitPattern(
            variable: StringOperand,
            separator: StringOperand,
            ignoreCase: BooleanOperand
        ): SplitPattern = SplitPattern(listOf(variable, separator, ignoreCase) as List<Variable>)
    }
}

@Serializable
public data class EqualsPattern(override val arguments: List<Variable>) : LogicExpression()

@Serializable
public data class Ascii(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Length(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Lower(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Upper(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class SubString(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Replace(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class ReplacePattern(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Reverse(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Trim(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class TrimStart(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class TrimEnd(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class PadStart(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class PadEnd(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Left(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Right(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Replicate(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class IndexOf(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class IndexOfPattern(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Space(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class Split(override val arguments: List<Variable>) : StringExpression()

@Serializable
public data class SplitPattern(override val arguments: List<Variable>) : StringExpression()

public interface Value<T> {
    public val value: T
}

@Serializable
public data class BooleanValue(override val value: Boolean) : BooleanVariable(), Value<Boolean>

@Serializable
public data class BooleanCollection(override val value: Collection<Boolean?>) : CollectionVariable(),
    Value<Collection<Boolean?>>

@Serializable
public data class UByteValue(override val value: UByte) : NumberVariable(), Value<UByte>

@Serializable
public data class UByteCollection(override val value: Collection<UByte?>) : CollectionVariable(),
    Value<Collection<UByte?>>

@Serializable
public data class ByteValue(override val value: Byte) : NumberVariable(), Value<Byte>

@Serializable
public data class ByteCollection(override val value: Collection<Byte?>) : CollectionVariable(), Value<Collection<Byte?>>

@Serializable
public data class UShortValue(override val value: UShort) : NumberVariable(), Value<UShort>

@Serializable
public data class UShortCollection(override val value: Collection<UShort?>) : CollectionVariable(),
    Value<Collection<UShort?>>

@Serializable
public data class ShortValue(override val value: Short) : NumberVariable(), Value<Short>

@Serializable
public data class ShortCollection(override val value: Collection<Short?>) : CollectionVariable(),
    Value<Collection<Short?>>

@Serializable
public data class UIntValue(override val value: UInt) : NumberVariable(), Value<UInt>

@Serializable
public data class UIntCollection(override val value: Collection<UInt?>) : CollectionVariable(), Value<Collection<UInt?>>

@Serializable
public data class IntValue(override val value: Int) : NumberVariable(), Value<Int>

@Serializable
public data class IntCollection(override val value: Collection<Int?>) : CollectionVariable(), Value<Collection<Int?>>

@Serializable
public data class ULongValue(override val value: ULong) : NumberVariable(), Value<ULong>

@Serializable
public data class ULongCollection(override val value: Collection<ULong?>) : CollectionVariable(),
    Value<Collection<ULong?>>

@Serializable
public data class LongValue(override val value: Long) : NumberVariable(), Value<Long>

@Serializable
public data class LongCollection(override val value: Collection<Long?>) : CollectionVariable(), Value<Collection<Long?>>

@Serializable
public data class FloatValue(override val value: Float) : NumberVariable(), Value<Float>

@Serializable
public data class FloatCollection(override val value: Collection<Float?>) : CollectionVariable(),
    Value<Collection<Float?>>

@Serializable
public data class DoubleValue(override val value: Double) : NumberVariable(), Value<Double>

@Serializable
public data class DoubleCollection(override val value: Collection<Double?>) : CollectionVariable(),
    Value<Collection<Double?>>

@Serializable
public data class CharValue(override val value: Char) : CharVariable(), Value<Char>

@Serializable
public data class CharCollection(override val value: Collection<Char?>) : CollectionVariable(), Value<Collection<Char?>>

@Serializable
public data class StringValue(override val value: String) : StringVariable(), Value<String>

@Serializable
public data class StringCollection(override val value: Collection<String?>) : CollectionVariable(),
    Value<Collection<String?>>

@Serializable
public data class BigIntegerValue(override val value: BigIntegerSerial) : ComparableVariable(), Value<BigInteger>

@Serializable
public data class BigIntegerCollection(override val value: Collection<BigIntegerSerial?>) : CollectionVariable(),
    Value<Collection<BigInteger?>>

@Serializable
public data class BigDecimalValue(override val value: BigDecimalSerial) : ComparableVariable(), Value<BigDecimal>

@Serializable
public data class BigDecimalCollection(override val value: Collection<BigDecimalSerial?>) : CollectionVariable(),
    Value<Collection<BigDecimal?>>

@Serializable
public data class LocalTimeValue(override val value: LocalTime) : TemporalVariable(), Value<LocalTime>

@Serializable
public data class LocalTimeCollection(override val value: Collection<LocalTime?>) : CollectionVariable(),
    Value<Collection<LocalTime?>>

@Serializable
public data class LocalDateValue(override val value: LocalDate) : TemporalVariable(), Value<LocalDate>

@Serializable
public data class LocalDateCollection(override val value: Collection<LocalDate?>) : CollectionVariable(),
    Value<Collection<LocalDate?>>

@Serializable
public data class LocalDateTimeValue(override val value: LocalDateTime) : TemporalVariable(), Value<LocalDateTime>

@Serializable
public data class LocalDateTimeCollection(override val value: Collection<LocalDateTime?>) : CollectionVariable(),
    Value<Collection<LocalDateTime?>>

@Serializable
public data class UUIDValue(override val value: UuidSerial) : ComparableVariable(), Value<Uuid>

@Serializable
public data class UUIDCollection(override val value: Collection<UuidSerial?>) : CollectionVariable(),
    Value<Collection<Uuid?>>

@Serializable
public data object NullValue : Variable(), Value<Nothing?>, ComparableOperand, BooleanOperand, NumberOperand,
    StringOperand,
    TemporalOperand {
    override val value: Nothing?
        get() = null
}

@Serializable
public data class GenericComparableValue<T>(val value: T) : ComparableVariable()

@Serializable
public data class Field(override val value: String) : Variable(),
    Value<String>, ComparableOperand, BooleanOperand, NumberOperand,
    StringOperand, TemporalOperand

@Serializable
public data class Projection(val value: String, val alias: String? = null, val distinct: Boolean = false) : Variable() {
    @Transient
    public val count: Count = AggregateExpression.count(this)

    public fun <T> max(): Max<T> = AggregateExpression.max(this)

    public fun <T> min(): Min<T> = AggregateExpression.min(this)

    public fun <T> avg(): Avg<T> = AggregateExpression.avg(this)

    public fun <T> sum(): Sum<T> = AggregateExpression.sum(this)
}