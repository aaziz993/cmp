package ai.tech.core.misc.plugin.serialization.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SerializationConfig(
    val json: JsonConfig? = null,
    val xml: XMLConfig? = null,
    val cbor: CBORConfig? = null,
    val protobuf: ProtobufConfig? = null,
    override val enable: Boolean = true,
) : EnabledConfig
