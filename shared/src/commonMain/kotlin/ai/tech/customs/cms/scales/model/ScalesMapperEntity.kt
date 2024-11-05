package customs.cms.scales.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class ScalesMapperEntity(
    val id: Long? = null,
    val at: String,
    val atFormat: String,
    val vehicleLicensePlate: String,
    val totalWeight: String,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)