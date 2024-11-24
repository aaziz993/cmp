package ai.tech.core.data.validator

import ai.tech.core.misc.type.multiple.AP
import ai.tech.core.misc.type.multiple.LDP
import ai.tech.core.misc.type.multiple.LLP
import ai.tech.core.misc.type.multiple.LP
import ai.tech.core.misc.type.multiple.LUP
import ai.tech.core.misc.type.multiple.quotePattern
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
    }
    else {
        message
    }

    public companion object {

        public fun nonEmpty(message: String): ValidatorRule = ValidatorRule(
                "$AP+",
                message = message,
        )

        public fun lengthInRange(min: Int, max: Int? = null, message: String): ValidatorRule = ValidatorRule(
                "$AP{$min,${max ?: ""}}",
                message = message,
        )

        public fun startsWith(value: String, ignoreCase: Boolean = false, message: String): ValidatorRule =
            ValidatorRule(
                    "^${value.quotePattern()}$AP*",
                    ignoreCase,
                    message,
            )

        public fun endsWith(value: String, ignoreCase: Boolean = false, message: String): ValidatorRule =
            ValidatorRule(
                    "$AP*${value.quotePattern()}$",
                    ignoreCase,
                    message,
            )

        public fun contains(value: String, ignoreCase: Boolean = false, message: String): ValidatorRule =
            ValidatorRule(
                    "$AP*${value.quotePattern()}$AP*",
                    ignoreCase,
                    message,
            )

        public fun isAlphaNumeric(message: String): ValidatorRule = ValidatorRule(
                "$LDP+",
                message = message,
        )

        public fun noDigits(message: String): ValidatorRule = ValidatorRule(
                "[^\\d]*",
                message = message,
        )

        public fun hasDigit(message: String): ValidatorRule = ValidatorRule(
                """$AP*\d+$AP*""",
                message = message,
        )

        public fun noLetters(message: String): ValidatorRule = ValidatorRule(
                "[^$LP]*",
                message = message,
        )

        public fun hasLetter(message: String): ValidatorRule = ValidatorRule(
                "$AP*$LP+$AP*",
                message = message,
        )

        public fun lettersOnly(message: String): ValidatorRule = ValidatorRule(
                "$LP+",
                message = message,
        )

        public fun digitsOnly(message: String): ValidatorRule = ValidatorRule(
                """\d+""",
                message = message,
        )

        public fun nonZero(message: String): ValidatorRule = ValidatorRule(
                """[+-]?[^0]$AP*""",
                message = message,
        )

        public fun positive(message: String): ValidatorRule = ValidatorRule(
                """[^-]$AP*""",
                message = message,
        )

        public fun negative(message: String): ValidatorRule = ValidatorRule(
                """-$AP*""",
                message = message,
        )

        public fun intValue(message: String): ValidatorRule = ValidatorRule(
                """[+-]?\d+""",
                message = message,
        )

        public fun uIntValue(message: String): ValidatorRule = ValidatorRule(
                """[^-]?\d+""",
                message = message,
        )

        public fun floatValue(message: String): ValidatorRule = ValidatorRule(
                """[+-]?\d+\.\d+""",
                message = message,
        )

        public fun isAsciiOnly(message: String): ValidatorRule = ValidatorRule(
                """[\\u0000-\\u007F]+""",
                message = message,
        )

        public fun lowercaseOnly(message: String): ValidatorRule = ValidatorRule(
                "[^$LUP]+",
                message = message,
        )

        public fun uppercaseOnly(message: String): ValidatorRule = ValidatorRule(
                "[^$LLP]+",
                message = message,
        )

        public fun noWhitespace(message: String): ValidatorRule = ValidatorRule(
                "[^ ]*",
                message = message,
        )

        public fun singleLine(message: String): ValidatorRule = ValidatorRule(
                """[^\r\n]*""",
                message = message,
        )

        public fun numericPhoneLength(message: String): ValidatorRule = Length.NumericPhoneNumber.let {
            lengthInRange(
                    it.first,
                    it.last,
                    message,
            )
        }

        public fun numericPhonePattern(message: String): ValidatorRule = ValidatorRule(
                Pattern.InternationalNumericPhoneNumber.pattern,
                message = message,
        )

        public fun delimitedPhoneLength(message: String): ValidatorRule = Length.DelimitedPhoneNumber.let {
            lengthInRange(
                    it.first,
                    it.last, message,
            )
        }

        public fun delimitedPhonePattern(message: String): ValidatorRule = ValidatorRule(
                Pattern.InternationalDelimitedPhoneNumber.pattern,
                message = message,
        )

        public fun emailLength(message: String): ValidatorRule = Length.Email.let {
            lengthInRange(it.first, it.last, message)
        }

        public fun emailPattern(message: String): ValidatorRule = ValidatorRule(
                Pattern.Email.pattern,
                message = message,
        )

        public fun kotlinDurationPattern(message: String): ValidatorRule = ValidatorRule(
                """^P(?!$)(\d+Y)?(\d+M)?(\d+D)?(T(\d+H)?(\d+M)?(\d+(\.\d+)?S)?)?$""",
                message = message,
        )
    }

    /**
     * Default field lengths for common input types used in applications.
     */
    public data object Length {

        /**
         * The smallest possible, most commonly used minimum and maximum url lengths in web browsers.
         * The value of 2048 is mostly used by IE up to 7.0.
         * The urls can be as small as one-character long.
         */
        public val Url: IntRange = 1..2048

        /**
         * A range of values the length of an email can take
         * The [RFC-5322](https://datatracker.ietf.org/doc/html/rfc5322) standard does not specify an exact limit on the maximum length of the email, however, it recommends
         * that the email is no longer than 256 characters as longer emails are not exactly useful to have.
         *
         */
        public val Email: IntRange = 3..256

        /**
         * A Bcrypt hash compliant password lengths with a sensible upper limit of 48 chars
         * A minimum length of a password using BCRYPT algorithm has to be at least 8 characters, as all other password
         * lengths can be cracked in seconds using the hash of the password using moder algorithms.
         */
        public val Password: IntRange = 8..48

        /**
         * An [E.164](https://en.wikipedia.org/wiki/E.164)-compliant numeric (no delimiters) phone number minimum and maximum length.
         * Note that the minimum length can vary and the lowest possible value is specified here.
         * @see DelimitedPhoneNumber
         */
        public val NumericPhoneNumber: IntRange = 3..15

        /**
         * An [E.164](https://en.wikipedia.org/wiki/E.164)-compliant delimited phone number minimum and maximum length.
         * Note that the minimum length can vary and the lowest possible value is specified here.
         * The maximum length is derived from the assumption that no more than 5 delimiters may be used (not in the standard).
         * @see DelimitedPhoneNumber
         */
        public val DelimitedPhoneNumber: IntRange = 3..20
    }

    /**
     * Common regular expressions used for form validation
     */
    @Suppress("MaxLineLength")
    public data object Pattern {

        /**
         * An international phone number regex, that does not allow delimiters.
         * - Only numbers are allowed
         * - the "+" sign at the start is required
         * The first group in this regex will be the country code.
         * @see InternationalDelimitedPhoneNumber
         */
        public val InternationalNumericPhoneNumber: Regex by lazy {
            """\+(9[976]\d|8[987530]\d|6[987]\d|5[90]\d|42\d|3[875]\d|2[98654321]\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1)\d{1,14}${'$'}""".toRegex()
        }

        /**
         * An international phone number regex with delimiters allowed.
         * - Any delimiters including spaces are allowed (\W char in regex). Strip them from your final number.
         * - Otherwise equal to [InternationalNumericPhoneNumber].
         * @See InternationalNumericPhoneNumber
         */
        public val InternationalDelimitedPhoneNumber: Regex by lazy {
            """^\+((?:9[679]|8[035789]|6[789]|5[90]|42|3[578]|2[1-689])|9[0-58]|8[1246]|6[0-6]|5[1-8]|4[013-9]|3[0-469]|2[70]|7|1)(?:\W*\d){0,13}\d$""".toRegex()
        }

        /**
         * An [RFC-5322](https://datatracker.ietf.org/doc/html/rfc5322)-compliant email regex. Most commonly used among web services.
         * Main features:
         * - Requires a domain part per 5322 standard.
         * - Does not limit the length of the input. Use other validations to limit the length according to your needs.
         */
        public val Email: Regex by lazy {
            """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".toRegex()
        }

        /**
         * A password-type regular expression with the following properties:
         * These password requirements are commonly used, but you are encouraged to use your own regex if needed
         * - At least one latin Uppercase letter
         * - At least one latin lowercase letter
         * - At least one digit
         * - Optional special characters (not required)
         * - Minimum length at least 8 characters
         * - Maximum can be any. Use other rules to specify max length
         */
        public val Password: Regex by lazy {
            """^(?=.*\p{Upper})(?=.*\p{Lower})(?=.*\d)[\p{Upper}\p{Lower}\d\p{Punct}]{8,}$""".toRegex()
        }

        /**
         * A web url starting with either http, https, www and then containing a domain-specific part and optionally a path
         */
        public val UrlPattern: Regex = Regex(
                """(http(s)?://.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&/=]*)""",
        )
    }
}
