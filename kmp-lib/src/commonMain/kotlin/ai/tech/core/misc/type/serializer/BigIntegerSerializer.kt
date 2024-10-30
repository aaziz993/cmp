package ai.tech.core.misc.type.serializer

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable

public object BigIntegerSerializer :
    PrimitiveSerializer<BigInteger>(
        BigInteger::class,
        { BigInteger.parseString(it) },
        { it.toString() }
    )


public typealias BigIntegerSerial = @Serializable(with = BigIntegerSerializer::class) BigInteger