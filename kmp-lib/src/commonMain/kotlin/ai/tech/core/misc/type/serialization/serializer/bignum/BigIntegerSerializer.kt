package ai.tech.core.misc.type.serialization.serializer.bignum

import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable

public object BigIntegerSerializer :
    PrimitiveStringSerializer<BigInteger>(
        BigInteger::class,
        BigInteger::toString,
        BigInteger::parseString,
    )


public typealias BigIntegerSerial = @Serializable(with = BigIntegerSerializer::class) BigInteger
