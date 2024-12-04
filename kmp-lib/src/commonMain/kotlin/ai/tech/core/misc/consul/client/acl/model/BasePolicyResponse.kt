package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial

public interface BasePolicyResponse {

    public val id: String
    public val name: String
    public val datacenters: String?
    public val hash: String
    public val createIndex: BigIntegerSerial
    public val modifyIndex: BigIntegerSerial
}
