package core.type.single

import java.math.BigDecimal
import java.math.BigInteger

public fun BigInteger.toKotlinBigInteger(): com.ionspin.kotlin.bignum.integer.BigInteger =
    com.ionspin.kotlin.bignum.integer.BigInteger.parseString(toString())

public fun BigDecimal.toKotlinBigDecimal(): com.ionspin.kotlin.bignum.decimal.BigDecimal =
    com.ionspin.kotlin.bignum.decimal.BigDecimal.parseString(toPlainString())