package ai.tech.core.data.crud.model

import kotlinx.serialization.Serializable

@Serializable
public data class Order(
    public val name: String,
    public val ascending: Boolean = true,
    public val nullFirst: Boolean? = null,
)
