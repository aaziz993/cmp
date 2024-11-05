package customs.cms.xray.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class XrayMapperEntity(
    val id: Long? = null,
    val at: String,
    val atFormat: String,
    val officerName: String,
    val vehicleLicensePlate: String,
    val countryOfDispatch: String,
    val goodsDescription: String?,
    val reason: String?,
    val image1: String? = null,
    val image2: String? = null,
    val image3: String? = null,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)