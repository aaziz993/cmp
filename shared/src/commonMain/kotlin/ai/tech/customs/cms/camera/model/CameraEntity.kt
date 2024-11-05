package customs.cms.camera.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class CameraEntity(
    val id: Long? = null,
    val customsOffice: String,
    val customsCode: String,
    val at: LocalDateTime,
    val vehicleLicensePlate: String? = null,
    val vehicleLicensePlateImage: String? = null,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)