package ai.tech.core.misc.type.serializer

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import ai.tech.core.misc.type.serializer.PrimitiveSerializer
import kotlinx.serialization.Serializable

public object BigDecimalSerializer :
    PrimitiveSerializer<BigDecimal>(
        BigDecimal::class,
        { BigDecimal.parseString(it) },
        { it.toString() }
    )

public typealias BigDecimalSerial = @Serializable(with = BigDecimalSerializer::class) BigDecimal