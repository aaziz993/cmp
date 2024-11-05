package customs.cms.camera.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class CameraMapperEntity(
    val id: Long? = null,
    val at: String,
    val atFormat: String,
    val vehicleLicensePlate: String,
    val vehicleLicensePlateImage: String? = null,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)