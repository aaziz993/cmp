package customs.cms.xray.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class XrayEntity(
    val id: Long? = null,
    val customsOffice: String,
    val customsCode: String,
    val at: LocalDateTime,
    val officerName: String? = null,
    val vehicleLicensePlate: String? = null,
    val countryOfDispatch: String? = null,
    val goodsDescription: String? = null,
    val reason: String? = null,
    val image1: String? = null,
    val image2: String? = null,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)