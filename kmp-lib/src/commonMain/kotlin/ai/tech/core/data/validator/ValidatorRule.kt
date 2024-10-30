package core.data.validator

import core.misc.type.multiple.AP
import core.misc.type.multiple.LDP
import core.misc.type.multiple.LLP
import core.misc.type.multiple.LP
import core.misc.type.multiple.LUP
import core.misc.type.multiple.quotePattern
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import pro.respawn.kmmutils.inputforms.default.FieldLengths
import pro.respawn.kmmutils.inputforms.default.Patterns

@Serializable
public data class ValidatorRule(
    public val pattern: String,
    public val ignoreCase: Boolean = true,
    public val message: String,
) {
    @Transient
    private val regex = pattern.toRegex()

    public fun validate(input: String): String? = if (regex.matches(input)) {
        null
    } else {
        message
    }

    public companion object {
        public fun nonEmpty(message: String): ValidatorRule = ValidatorRule(
            "$AP+",
            message = message
        )

        public fun lengthInRange(min: Int, max: Int? = null, message: String): ValidatorRule = ValidatorRule(
            "$AP{$min,${max ?: ""}}",
            message = message
        )


        public fun startsWith(value: String, ignoreCase: Boolean = false, message: String): ValidatorRule =
            ValidatorRule(
                "^${value.quotePattern()}$AP*",
                ignoreCase,
                message
            )

        public fun endsWith(value: String, ignoreCase: Boolean = false, message: String): ValidatorRule =
            ValidatorRule(
                "$AP*${value.quotePattern()}$",
                ignoreCase,
                message
            )

        public fun contains(value: String, ignoreCase: Boolean = false, message: String): ValidatorRule =
            ValidatorRule(
                "$AP*${value.quotePattern()}$AP*",
                ignoreCase,
                message
            )

        public fun isAlphaNumeric(message: String): ValidatorRule = ValidatorRule(
            "$LDP+",
            message = message
        )

        public fun noDigits(message: String): ValidatorRule = ValidatorRule(
            "[^\\d]*",
            message = message
        )

        public fun hasDigit(message: String): ValidatorRule = ValidatorRule(
            """$AP*\d+$AP*""",
            message = message
        )

        public fun noLetters(message: String): ValidatorRule = ValidatorRule(
            "[^$LP]*",
            message = message
        )

        public fun hasLetter(message: String): ValidatorRule = ValidatorRule(
            "$AP*$LP+$AP*",
            message = message
        )

        public fun lettersOnly(message: String): ValidatorRule = ValidatorRule(
            "$LP+",
            message = message
        )

        public fun digitsOnly(message: String): ValidatorRule = ValidatorRule(
            """\d+""",
            message = message
        )

        public fun nonZero(message: String): ValidatorRule = ValidatorRule(
            """[+-]?[^0]$AP*""",
            message = message
        )

        public fun positive(message: String): ValidatorRule = ValidatorRule(
            """[^-]$AP*""",
            message = message
        )

        public fun negative(message: String): ValidatorRule = ValidatorRule(
            """-$AP*""",
            message = message
        )

        public fun intValue(message: String): ValidatorRule = ValidatorRule(
            """[+-]?\d+""",
            message = message
        )

        public fun floatValue(message: String): ValidatorRule = ValidatorRule(
            """[+-]?\d+\.\d+""",
            message = message
        )

        public fun isAsciiOnly(message: String): ValidatorRule = ValidatorRule(
            """[\\u0000-\\u007F]+""",
            message = message
        )

        public fun lowercaseOnly(message: String): ValidatorRule = ValidatorRule(
            "[^$LUP]+",
            message = message
        )

        public fun uppercaseOnly(message: String): ValidatorRule = ValidatorRule(
            "[^$LLP]+",
            message = message
        )

        public fun noWhitespace(message: String): ValidatorRule = ValidatorRule(
            "[^ ]*",
            message = message
        )

        public fun singleLine(message: String): ValidatorRule = ValidatorRule(
            """[^\r\n]*""",
            message = message
        )

        public fun numericPhoneLength(message: String): ValidatorRule = FieldLengths.NumericPhoneNumber.let {
            lengthInRange(
                it.first,
                it.last,
                message
            )
        }

        public fun numericPhonePattern(message: String): ValidatorRule = ValidatorRule(
            Patterns.InternationalNumericPhoneNumber.pattern,
            message = message
        )

        public fun delimitedPhoneLength(message: String): ValidatorRule = FieldLengths.DelimitedPhoneNumber.let {
            lengthInRange(
                it.first,
                it.last, message
            )
        }

        public fun delimitedPhonePattern(message: String): ValidatorRule = ValidatorRule(
            Patterns.InternationalDelimitedPhoneNumber.pattern,
            message = message
        )

        public fun emailLength(message: String): ValidatorRule = FieldLengths.Email.let {
            lengthInRange(it.first, it.last, message)
        }

        public fun emailPattern(message: String): ValidatorRule = ValidatorRule(
            Patterns.Email.pattern,
            message = message
        )
    }
}
