package core.data.validator

import kotlinx.serialization.Serializable

@Serializable
public data class Validator(
    public val type: ValidationType = ValidationType.FAIL_FAST,
    public val rules: List<ValidatorRule>,
    public val required: Boolean = true,
) {
    public fun validate(input: String): List<String> =
        if (input.isEmpty() && !required) {
            emptyList()
        } else when (type) {
            ValidationType.FAIL_FAST -> rules.firstNotNullOfOrNull { it.validate(input) }?.let { listOf(it) }
                .orEmpty()

            ValidationType.LAZY_EVAL -> rules.mapNotNull { it.validate(input) }
        }

    public companion object {
        public fun nonEmpty(emptyMessage: String = "value_is_empty"): Validator = Validator(
            rules = listOf(
                ValidatorRule.nonEmpty(emptyMessage)
            )
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
                ValidatorRule.nonEmpty(emptyMessage),
                ValidatorRule.numericPhoneLength(lengthMessage),
                ValidatorRule.startsWith("+", message = prefixMessage),
                ValidatorRule.hasDigit(digitsMessage),
                ValidatorRule.noLetters(lettersMessage),
                ValidatorRule.numericPhonePattern(patternMessage),
            ),
            required = required
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
                ValidatorRule.nonEmpty(emptyMessage),
                ValidatorRule.delimitedPhoneLength(lengthMessage),
                ValidatorRule.startsWith("+", message = prefixMessage),
                ValidatorRule.hasDigit(digitsMessage),
                ValidatorRule.noLetters(lettersMessage),
                ValidatorRule.delimitedPhonePattern(patternMessage),
            ),
            required = required
        )

        public fun email(
            required: Boolean = true,
            emptyMessage: String = "value_is_empty",
            lengthMessage: String = "value_length_is_invalid",
            whitespaceMessage: String = "value_has_whitespace",
            patternMessage: String = "value_is_invalid",
        ): Validator = Validator(
            rules = listOf(
                ValidatorRule.nonEmpty(emptyMessage),
                ValidatorRule.emailLength(lengthMessage),
                ValidatorRule.noWhitespace(whitespaceMessage),
                ValidatorRule.emailPattern(patternMessage)
            ),
            required = required
        )
    }
}
