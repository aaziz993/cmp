package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial
import kotlinx.datetime.LocalDate

public interface BaseTokenResponse {

    public val accessorId: String
    public val description: String
    public val policies: List<Token.PolicyLink>
    public val createIndex: BigIntegerSerial
    public val modifyIndex: BigIntegerSerial
    public val local: Boolean
    public val createTime: LocalDate
    public val hash: String
}
