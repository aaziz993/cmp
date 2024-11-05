package customs.cms.model

import core.auth.model.AuthResource
import core.presentation.model.config.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CMSScreenConfig(
    override val route: String,
    override val auth: AuthResource,
    val scan: ScanConfig,
    val admin: ScanAdminConfig,
) : DestinationConfig