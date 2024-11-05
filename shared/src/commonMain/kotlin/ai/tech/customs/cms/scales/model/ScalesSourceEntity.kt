package customs.cms.scales.model

import customs.cms.model.ScanSource
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
public data class ScalesSourceEntity(
    val id: Long? = null,
    override val customsOffice: String,
    override val customsCode: String,
    override val path: String,
    override val dataPath: String? = null,
    override val isFile: Boolean = false,
    override val mapperId: Long,
    val createdBy: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
) : ScanSource