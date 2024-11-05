package ai.tech.core.misc.location.model

import ai.tech.core.data.database.crud.model.Entity
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class LocationEntity(
    override val latitude: Double,
    override val longitude: Double,
    override val altitude: Double = 0.0,
    override val identifier: String? = null,
    override val description: String? = null,
    override val id: Long? = null,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
) : Location, Entity<Long>
