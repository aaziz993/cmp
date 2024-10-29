package ai.tech.core

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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

public const val DEFAULT_BUFFER_SIZE: Int = 4096