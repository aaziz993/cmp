package ai.tech.core.data.validator

import kotlinx.serialization.Serializable

@Serializable
public data class Validator(
    public val type: Validation = Validation.FAIL_FAST,
    public val rules: List<ValidatorRule>,
    public val required: Boolean = true,
) {

    public fun validate(input: String): List<String> =
        if (input.isEmpty() && !required) {
            emptyList()
        }
        else when (type) {
            Validation.FAIL_FAST -> rules.firstNotNullOfOrNull { it.validate(input) }?.let { listOf(it) }
                .orEmpty()

            Validation.LAZY_EVAL -> rules.mapNotNull { it.validate(input) }
        }

    public companion object {

        public fun nonEmpty(emptyMessage: String = "value_is_empty"): Validator = Validator(
            rules = listOf(
                ValidatorRule.Companion.nonEmpty(emptyMessage),
            ),
        )

        public fun numericPhone(
            required: Boolean = true,
            emptyMessage: String = "value_is_empty",
            lengthMessage: String = "value_length_is_invalid",
            prefixMessage: String = "value_prefix_is_not_plus",
            digitsMessage: String = "value_has_not_digits",
            lettersMessage: String = "value_has_letters",
            patternMessage: String = "value_is_invalid",
        ): Validator = Validator(
            rules = listOf(
                ValidatorRule.Companion.nonEmpty(emptyMessage),
                ValidatorRule.Companion.numericPhoneLength(lengthMessage),
                ValidatorRule.Companion.startsWith("+", message = prefixMessage),
                ValidatorRule.Companion.hasDigit(digitsMessage),
                ValidatorRule.Companion.noLetters(lettersMessage),
                ValidatorRule.Companion.numericPhonePattern(patternMessage),
            ),
            required = required,
        )

        public fun delimitedPhone(
            required: Boolean = true,
            emptyMessage: String = "value_is_empty",
            lengthMessage: String = "value_length_is_invalid",
            prefixMessage: String = "value_prefix_is_not_plus",
            digitsMessage: String = "value_has_not_digits",
            lettersMessage: String = "value_has_letters",
            patternMessage: String = "value_is_invalid",
        ): Validator = Validator(
            rules = listOf(
                ValidatorRule.Companion.nonEmpty(emptyMessage),
                ValidatorRule.Companion.delimitedPhoneLength(lengthMessage),
                ValidatorRule.Companion.startsWith("+", message = prefixMessage),
                ValidatorRule.Companion.hasDigit(digitsMessage),
                ValidatorRule.Companion.noLetters(lettersMessage),
                ValidatorRule.Companion.delimitedPhonePattern(patternMessage),
            ),
            required = required,
        )

        public fun email(
            required: Boolean = true,
            emptyMessage: String = "value_is_empty",
            lengthMessage: String = "value_length_is_invalid",
            whitespaceMessage: String = "value_has_whitespace",
            patternMessage: String = "value_is_invalid",
        ): Validator = Validator(
            rules = listOf(
                ValidatorRule.Companion.nonEmpty(emptyMessage),
                ValidatorRule.Companion.emailLength(lengthMessage),
                ValidatorRule.Companion.noWhitespace(whitespaceMessage),
                ValidatorRule.Companion.emailPattern(patternMessage),
            ),
            required = required,
        )

        public fun kotlinDuration(
            required: Boolean = true,
            patternMessage: String = "value_is_invalid_duration",
        ): Validator = Validator(
            rules = listOf(
                ValidatorRule.Companion.kotlinDurationPattern(patternMessage),
            ),
            required = required,
        )
    }
}
