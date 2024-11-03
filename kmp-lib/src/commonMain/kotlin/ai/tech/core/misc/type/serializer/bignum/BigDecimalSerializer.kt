package ai.tech.core.misc.type.serializer.bignum

import ai.tech.core.misc.type.serializer.primitive.PrimitiveStringSerializer
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.Serializable

public object BigDecimalSerializer :
    PrimitiveStringSerializer<BigDecimal>(
        BigDecimal::class,
        BigDecimal::toString,
        BigDecimal::parseString,
    )

public typealias BigDecimalSerial = @Serializable(with = BigDecimalSerializer::class) BigDecimal
