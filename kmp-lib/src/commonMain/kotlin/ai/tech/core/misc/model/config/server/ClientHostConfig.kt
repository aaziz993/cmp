package ai.tech.core.misc.model.config.server

import kotlinx.serialization.Serializable

@Serializable
public data class ClientHostConfig(
    override val host: String,
    override val port: Int
) : SharedHostConfig
