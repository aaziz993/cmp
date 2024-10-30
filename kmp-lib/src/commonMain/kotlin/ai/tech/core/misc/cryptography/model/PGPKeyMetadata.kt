package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
public class PGPKeyMetadata(
    public val fingerprint: String,
    public val userIDs: List<PGPUserId>,
    public val createDate: Long,
    public val expireDate: Long,
)
