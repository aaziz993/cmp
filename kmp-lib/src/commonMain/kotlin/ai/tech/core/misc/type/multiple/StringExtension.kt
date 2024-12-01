package ai.tech.core.misc.type.multiple

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import ai.tech.core.data.model.Charset
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.toByteArray
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.Boolean
import kotlin.Byte
import kotlin.Char
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.UByte
import kotlin.UInt
import kotlin.ULong
import kotlin.UShort
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.time.Duration

// Line break pattern
public const val LBP: String = """(\r?\n|\n)"""

// Any pattern (space and non-space)
public const val AP: String = """[\s\S]"""

// Letter uppercase pattern
public const val LUP: String = "[A-ZА-Я]"

// Letter lowercase pattern
public const val LLP: String = "[a-zа-я]"

// Letter lowercase to uppercase pattern
public const val LLUP: String = "(?<=$LLP)(?=$LUP)"

// Letter uppercase to lowercase pattern
public const val LULP: String = "(?<=$LUP)(?=$LLP)"

// Letter uppercase to uppercase pattern
public const val LUUP: String = "(?<=$LUP)(?=$LUP)"

// Letter pattern
public const val LP: String = "[$LLP$LUP]"

// Letter and digit pattern
public const val LDP: String = """[$LP\d]"""

private val stringExtensionRegexMap: Map<String, Regex> =
    mapOf(
        "json" to """^\s*(\{$AP*\}|\[$AP*\])\s*$""".toRegex(),
        "xml" to """^\s*<\?xml[\s\S]*""".toRegex(),
        "html" to """^\s*<(!DOCTYPE +)?html$AP*""".toRegex(),
        "yaml" to """^( *((#|[^{\s]*:|-).*)?$LBP?)+$""".toRegex(),
        "properties" to """^( *((#|[^{\s\[].*?=).*)?$LBP?)+$""".toRegex(),
        "toml" to """^( *(([#\[\]"{}]|.*=).*)?$LBP?)+$""".toRegex(),
    )

public val String.extension: String?
    get() = stringExtensionRegexMap.entries.find { (_, r) -> r.matches(this) }?.key

public val stringFormatRegex: Regex = Regex("""%(\d)\$[ds]""")

public fun String.replaceWithArgs(args: List<String>) = stringFormatRegex.replace(this) { matchResult ->
    args[matchResult.groupValues[1].toInt() - 1]
}

public fun randomString(length: Int, charPool: List<Char> = ('a'..'z') + ('A'..'Z')): String = (1..length)
    .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
    .joinToString("")

public fun String.quotePattern(): String = """\Q$this\E"""

public fun String.toTemporal(kClass: KClass<*>): Any =
    when (kClass) {
        LocalTime::class -> LocalTime.parse(this)
        LocalDate::class -> LocalDate.parse(this)
        LocalDateTime::class -> LocalDateTime.parse(this)
        Duration::class -> Duration.parse(this)
        DatePeriod::class -> DatePeriod.parse(this)
        DateTimePeriod::class -> DateTimePeriod.parse(this)
        else -> IllegalArgumentException("Can't convert \"$this\" to \"${kClass.simpleName}\"")
    }

public fun String.toPrime(kClass: KClass<*>): Any =
    when (kClass) {
        Boolean::class -> toBoolean()
        UByte::class -> toUByte()
        UShort::class -> toUShort()
        UInt::class -> toUInt()
        ULong::class -> toULong()
        Byte::class -> toByte()
        Short::class -> toShort()
        Int::class -> toInt()
        Long::class -> toLong()
        Float::class -> toFloat()
        Double::class -> toDouble()
        Char::class -> this[0]
        String::class -> this
        BigInteger::class -> BigInteger.parseString(this)
        BigDecimal::class -> BigDecimal.parseString(this)
        else -> toTemporal(kClass)
    }

public fun matcher(
    caseMatch: Boolean = true,
    wordMatch: Boolean = true,
    regexMatch: Boolean = false,
): (String, String) -> Boolean =
    when {
        regexMatch -> {
            val regexMatcher: (String) -> Regex =
                if (caseMatch) {
                    { pattern -> Regex(pattern) }
                }
                else {
                    { pattern -> Regex(pattern, RegexOption.IGNORE_CASE) }
                }
            if (wordMatch) {
                { str, pattern -> regexMatcher(pattern).matches(str) }
            }
            else {
                { str, pattern -> regexMatcher(pattern).containsMatchIn(str) }
            }
        }

        wordMatch ->
            { str1, str2 -> str1.equals(str2, !caseMatch) }

        else ->
            { str1, str2 ->
                str1.contains(
                    str2,
                    !caseMatch,
                )
            }
    }

// ///////////////////////////////////////////////////////ENUM//////////////////////////////////////////////////////////
public inline fun <reified T : Enum<T>> String.enumValueOf(): T = enumValueOf(this)

// ///////////////////////////////////////////////////////ARRAY//////////////////////////////////////////////////////////
public fun String.encode(charset: Charset = Charset.UTF_8): ByteArray = toByteArray(Charsets.forName(charset.name))
