@file:OptIn(ExperimentalSerializationApi::class)

package ai.tech.core.misc.type

import ai.tech.core.misc.type.accessor.Accessor
import ai.tech.core.misc.type.accessor.ListAccessor
import ai.tech.core.misc.type.accessor.MapLikeAccessor
import ai.tech.core.misc.type.multiple.depthIterator
import ai.tech.core.misc.type.single.parseOrNull
import ai.tech.core.misc.type.single.uuidFromOrNull
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind

public const val BOOLEAN_DEFAULT: Boolean = false

public val UBYTE_DEFAULT: UByte = 0U.toUByte()

public val USHORT_DEFAULT: UShort = 0U.toUShort()

public const val UINT_DEFAULT: UInt = 0U

public const val ULONG_DEFAULT: ULong = 0UL

public const val BYTE_DEFAULT: Byte = 0.toByte()

public const val SHORT_DEFAULT: Short = 0.toShort()

public const val INT_DEFAULT: Int = 0

public const val LONG_DEFAULT: Long = 0L

public const val FLOAT_DEFAULT: Float = 0F

public const val DOUBLE_DEFAULT: Double = 0.0

public const val CHAR_DEFAULT: Char = ' '

public const val STRING_DEFAULT: String = ""

public val BIGINTEGER_DEFAULT: BigInteger = BigInteger.ZERO

public val BIGDECIMAL_DEFAULT: BigDecimal = BigDecimal.ZERO

public val LOCALTIME_DEFAULT: LocalTime = LocalTime(0, 0)

public val LOCALDATE_DEFAULT: LocalDate = LocalDate(0, 0, 0)

public val LOCALDATETIME_DEFAULT: LocalDateTime = LocalDateTime(0, 0, 0, 0, 0)

public val DURATION_DEFAULT: Duration = 0.toDuration(DurationUnit.SECONDS)

public val DATEPERIOD_DEFAULT: DatePeriod = DatePeriod()

public val DATETIMEPERIOD_DEFAULT: DateTimePeriod = DateTimePeriod()

public val UUID_DEFAULT: Uuid
    get() = uuid4()

public val KClass<*>.isUIntNumber: Boolean
    get() = when (this) {
        UByte::class, UShort::class, UInt::class, ULong::class -> true
        else -> false
    }

public val KClass<*>.isIntNumber: Boolean
    get() = when (this) {
        Byte::class, Short::class, Int::class, Long::class -> true
        else -> false
    }

public val KClass<*>.isFloatNumber: Boolean
    get() = when (this) {
        Float::class, Double::class -> true
        else -> false
    }

public val KClass<*>.isBigNumber: Boolean
    get() = when (this) {
        BigInteger::class, BigDecimal::class -> true
        else -> false
    }

public val KClass<*>.isNumber: Boolean
    get() = isUIntNumber || isIntNumber || isFloatNumber || isBigNumber

public val KClass<*>.isChar: Boolean
    get() = this == Char::class

public val KClass<*>.isString: Boolean
    get() = this == BigDecimal::class

public val KClass<*>.isSymbolic: Boolean
    get() = isChar || isString

public val KClass<*>.isTemporal: Boolean
    get() = when (this) {
        LocalTime::class, LocalDate::class, LocalDateTime::class, Duration::class, DatePeriod::class, DateTimePeriod::class -> true
        else -> false
    }

public val KClass<*>.isPrime: Boolean
    get() = isNumber || isSymbolic || isTemporal || this == Uuid::class

public val KClass<*>.isList: Boolean
    get() = when (this) {
        List::class, MutableList::class, ArrayList::class -> true
        else -> false
    }

public val KClass<*>.isSet: Boolean
    get() = when (this) {
        Set::class, MutableSet::class, HashSet::class, LinkedHashSet::class -> true
        else -> false
    }

public val KClass<*>.isMap: Boolean
    get() = when (this) {
        Map::class, MutableMap::class, HashMap::class, LinkedHashMap::class -> true
        else -> false
    }

public fun KClass<*>.primeDefault(
    booleanDefault: Boolean = BOOLEAN_DEFAULT,
    uByteDefault: UByte = UBYTE_DEFAULT,
    uShortDefault: UShort = USHORT_DEFAULT,
    uIntDefault: UInt = UINT_DEFAULT,
    uLongDefault: ULong = ULONG_DEFAULT,
    byteDefault: Byte = BYTE_DEFAULT,
    shortDefault: Short = SHORT_DEFAULT,
    intDefault: Int = INT_DEFAULT,
    longDefault: Long = LONG_DEFAULT,
    floatDefault: Float = FLOAT_DEFAULT,
    doubleDefault: Double = DOUBLE_DEFAULT,
    charDefault: Char = CHAR_DEFAULT,
    stringDefault: String = STRING_DEFAULT,
    bigIntegerDefault: BigInteger = BIGINTEGER_DEFAULT,
    bigDecimalDefault: BigDecimal = BIGDECIMAL_DEFAULT,
    localTimeDefault: LocalTime = LOCALTIME_DEFAULT,
    localDateDefault: LocalDate = LOCALDATE_DEFAULT,
    localDateTimeDefault: LocalDateTime = LOCALDATETIME_DEFAULT,
    durationDefault: Duration = DURATION_DEFAULT,
    datePeriodDefault: DatePeriod = DATEPERIOD_DEFAULT,
    dateTimePeriodDefault: DateTimePeriod = DATETIMEPERIOD_DEFAULT,
    uuidDefault: () -> Uuid = { UUID_DEFAULT }
): Any =
    when (this) {
        Boolean::class -> booleanDefault

        UByte::class -> uByteDefault

        UShort::class -> uShortDefault

        UInt::class -> uIntDefault

        ULong::class -> uLongDefault

        Byte::class -> byteDefault

        Short::class -> shortDefault

        Int::class -> intDefault

        Long::class -> longDefault

        Float::class -> floatDefault

        Double::class -> doubleDefault

        Char::class -> charDefault

        String::class -> stringDefault

        BigInteger::class -> bigIntegerDefault

        BigDecimal::class -> bigDecimalDefault

        LocalTime::class -> localTimeDefault

        LocalDate::class -> localDateDefault

        LocalDateTime::class -> localDateTimeDefault

        Duration::class -> durationDefault

        DatePeriod::class -> datePeriodDefault

        DateTimePeriod::class -> dateTimePeriodDefault

        Uuid::class -> uuidDefault()

        else -> throw IllegalArgumentException("Unknown type \"$simpleName\"")
    }

public fun KClass<*>.parsePrimeOrNull(
    value: String,
    dateTimeFormat: DateTimeFormat<DateTimeComponents>? = null,
): Any? =
    when (this) {
        Boolean::class -> value.toBooleanStrictOrNull()

        UByte::class -> value.toUByteOrNull()

        UShort::class -> value.toUShortOrNull()

        UInt::class -> value.toUIntOrNull()

        ULong::class -> value.toULongOrNull()

        Byte::class -> value.toByteOrNull()

        Short::class -> value.toShortOrNull()

        Int::class -> value.toIntOrNull()

        Long::class -> value.toLongOrNull()

        Float::class -> value.toFloatOrNull()

        Double::class -> value.toDoubleOrNull()

        Char::class -> value[0]

        String::class -> value

        BigInteger::class -> BigInteger.parseOrNull(value)

        BigDecimal::class -> BigDecimal.parseOrNull(value)

        LocalTime::class -> dateTimeFormat?.parseOrNull(value)?.toLocalTime()
            ?: LocalTime.parseOrNull(value)

        LocalDate::class -> dateTimeFormat?.parseOrNull(value)?.toLocalDate()
            ?: LocalDate.parseOrNull(value)

        LocalDateTime::class -> dateTimeFormat?.parseOrNull(value)?.toLocalDateTime()
            ?: LocalDateTime.parseOrNull(value)

        Duration::class -> Duration.parseOrNull(value)

        DatePeriod::class -> DatePeriod.parseOrNull(value)

        DateTimePeriod::class -> DateTimePeriod.parseOrNull(value)

        Uuid::class -> uuidFromOrNull(value)

        else -> null
    }

public fun KClass<*>.parsePrime(
    value: String,
    dateTimeFormat: DateTimeFormat<DateTimeComponents>? = null,
): Any = parsePrimeOrNull(value, dateTimeFormat)
    ?: throw IllegalArgumentException("Unknown type \"$simpleName\"")

public val KType.kClass: KClass<*>
    get() = classifier as KClass<*>

public fun KType.primeDefault(
    booleanDefault: Boolean = BOOLEAN_DEFAULT,
    uByteDefault: UByte = UBYTE_DEFAULT,
    uShortDefault: UShort = USHORT_DEFAULT,
    uIntDefault: UInt = UINT_DEFAULT,
    uLongDefault: ULong = ULONG_DEFAULT,
    byteDefault: Byte = BYTE_DEFAULT,
    shortDefault: Short = SHORT_DEFAULT,
    intDefault: Int = INT_DEFAULT,
    longDefault: Long = LONG_DEFAULT,
    floatDefault: Float = FLOAT_DEFAULT,
    doubleDefault: Double = DOUBLE_DEFAULT,
    charDefault: Char = CHAR_DEFAULT,
    stringDefault: String = STRING_DEFAULT,
    bigIntegerDefault: BigInteger = BIGINTEGER_DEFAULT,
    bigDecimalDefault: BigDecimal = BIGDECIMAL_DEFAULT,
    localTimeDefault: LocalTime = LOCALTIME_DEFAULT,
    localDateDefault: LocalDate = LOCALDATE_DEFAULT,
    localDateTimeDefault: LocalDateTime = LOCALDATETIME_DEFAULT,
    durationDefault: Duration = DURATION_DEFAULT,
    datePeriodDefault: DatePeriod = DATEPERIOD_DEFAULT,
    dateTimePeriodDefault: DateTimePeriod = DATETIMEPERIOD_DEFAULT,
    uuidDefault: () -> Uuid = { UUID_DEFAULT },
    nullIfNullable: Boolean = true,
): Any? = if (isMarkedNullable && nullIfNullable) {
    null
}
else {
    kClass.primeDefault(
        booleanDefault,
        uByteDefault,
        uShortDefault,
        uIntDefault,
        uLongDefault,
        byteDefault,
        shortDefault,
        intDefault,
        longDefault,
        floatDefault,
        doubleDefault,
        charDefault,
        stringDefault,
        bigIntegerDefault,
        bigDecimalDefault,
        localTimeDefault,
        localDateDefault,
        localDateTimeDefault,
        durationDefault,
        datePeriodDefault,
        dateTimePeriodDefault,
        uuidDefault,
    )
}

@OptIn(ExperimentalSerializationApi::class)
public fun SerialDescriptor.getElementDescriptor(name: String): SerialDescriptor =
    getElementDescriptor(getElementIndex(name))

private val stringToTypeMap = mapOf(
    "kotlin.Boolean" to typeOf<Boolean>(),
    "kotlin.Boolean?" to typeOf<Boolean?>(),
    "kotlin.UByte" to typeOf<UByte>(),
    "kotlin.UByte?" to typeOf<UByte?>(),
    "kotlin.UShort" to typeOf<UShort>(),
    "kotlin.UShort?" to typeOf<UShort?>(),
    "kotlin.UInt" to typeOf<UInt>(),
    "kotlin.UInt?" to typeOf<UInt?>(),
    "kotlin.ULong" to typeOf<ULong>(),
    "kotlin.ULong?" to typeOf<ULong?>(),
    "kotlin.Byte" to typeOf<Byte>(),
    "kotlin.Byte?" to typeOf<Byte?>(),
    "kotlin.Short" to typeOf<Short>(),
    "kotlin.Short?" to typeOf<Short?>(),
    "kotlin.Int" to typeOf<Int>(),
    "kotlin.Int?" to typeOf<Int?>(),
    "kotlin.Long" to typeOf<Long>(),
    "kotlin.Long?" to typeOf<Long?>(),
    "kotlin.Float" to typeOf<Float>(),
    "kotlin.Float?" to typeOf<Float?>(),
    "kotlin.Double" to typeOf<Double>(),
    "kotlin.Double?" to typeOf<Double?>(),
    "com.ionspin.kotlin.bignum.integer.BigInteger" to typeOf<BigInteger>(),
    "com.ionspin.kotlin.bignum.integer.BigInteger?" to typeOf<BigInteger?>(),
    "com.ionspin.kotlin.bignum.decimal.BigDecimal" to typeOf<BigDecimal>(),
    "com.ionspin.kotlin.bignum.decimal.BigDecimal?" to typeOf<BigDecimal?>(),
    "kotlin.Char" to typeOf<Char>(),
    "kotlin.Char?" to typeOf<Char?>(),
    "kotlin.String" to typeOf<String>(),
    "kotlin.String?" to typeOf<String?>(),
    "kotlin.LocalTime" to typeOf<LocalTime>(),
    "kotlin.LocalTime?" to typeOf<LocalTime?>(),
    "kotlin.LocalDate" to typeOf<LocalDate>(),
    "kotlin.LocalDate?" to typeOf<LocalDate?>(),
    "kotlin.LocalDateTime" to typeOf<LocalDateTime>(),
    "kotlin.LocalDateTime?" to typeOf<LocalDateTime?>(),
    "com.benasher44.uuid.Uuid" to typeOf<Uuid>(),
    "com.benasher44.uuid.Uuid?" to typeOf<Uuid?>(),
)

public val SerialDescriptor.primeTypeOrNull: KType?
    get() = stringToTypeMap[serialName]

public val SerialDescriptor.primeType: KType
    get() = primeTypeOrNull!!

public val SerialDescriptor.isEnum: Boolean
    get() = kind == SerialKind.ENUM

// ///////////////////////////////////////////////////////ACCESSOR///////////////////////////////////////////////////////
internal fun Any.accessor(parentKey: Any? = null) = when (this) {
    is List<*> -> ListAccessor(this, parentKey)

    is Map<*, *> -> MapLikeAccessor(this, this, parentKey)

    else -> MapLikeAccessor(this, json.encodeToAny(this) as Map<*, *>, parentKey)
}

public fun <T : Any> T.accessorOrNull(
    keys: List<Any?>,
    accessor: (List<Accessor>, key: Any?, value: Any?) -> Accessor? = { _, key, value -> value?.accessor(key) },
): Accessor? = accessor(emptyList(), null, this)?.let {
    keys.fold(listOf(it)) { acc, key ->
        val accessor = acc.last()

        val value = accessor[key]

        val newAccessor = accessor(acc, key, value)

        if (newAccessor == null) {
            return null
        }

        if (value == null) {
            accessor[key] = it.instance
        }

        acc + listOf(newAccessor)
    }.last()
}

public fun <T : Any> T.contains(
    keys: List<Any?>,
    accessor: (List<Accessor>, key: Any?, value: Any?) -> Accessor? = { _, key, value -> value?.accessor(key) },
): Boolean = accessorOrNull(keys.dropLast(1), accessor)?.contains(keys.last()) == true

public fun <T : Any> T.get(
    keys: List<Any?>,
    accessor: (List<Accessor>, key: Any?, value: Any?) -> Accessor? = { _, key, value -> value?.accessor(parentKey = key) },
): Any? = accessorOrNull(keys.dropLast(1), accessor)?.get(keys.last())

public fun <T : Any> T.set(
    keys: List<Any?>,
    value: Any?,
    accessor: (List<Accessor>, key: Any?, value: Any?) -> Accessor? = { _, key, value ->
        value?.accessor(parentKey = key) ?: mutableMapOf<Any?, Any?>().accessor(key)
    },
): Unit = accessorOrNull(keys.dropLast(1), accessor)!!.set(keys.last(), value)

public fun <T : Any> T.remove(
    keys: List<Any?>,
    accessor: (List<Accessor>, key: Any?, value: Any?) -> Accessor? = { _, key, value -> value?.accessor(key) },
): Any? = accessorOrNull(keys.dropLast(1), accessor)?.remove(keys.last())

// //////////////////////////////////////////////////////TRAVERSER///////////////////////////////////////////////////////
public fun Any.mapTo(
    value: Any,
    keyTransform: (List<Accessor>, key: Any?) -> Any? = { _, key -> key },
    valueTransform: (List<Accessor>, key: String, value: Any?) -> Any? = { _, _, value -> value },
    accessor: (List<Accessor>, key: Any?, value: Any?) -> Accessor? = { _, key, value -> value?.accessor(key) },
) {
}

@Suppress("UNCHECKED_CAST")
public fun <T : Any> T.transform(
    transform: (transforms: List<T>, value: Any?) -> Iterator<Any?>?,
    removeLast: (transforms: List<T>, transform: Any?) -> Unit
) {
    val transforms = mutableListOf(this)

    transform(emptyList(), this)?.depthIterator(
        { _, value -> transform(transforms, value)?.also { transforms.add(value as T) } },
    ) {
        val last = transforms.removeLast()

        removeLast(transforms, last)
    }?.forEach { _ -> }
}
