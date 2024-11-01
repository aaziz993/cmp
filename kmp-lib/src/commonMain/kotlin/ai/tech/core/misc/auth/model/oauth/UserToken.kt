package ai.tech.core.misc.auth.model.oauth

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class UserToken(
    val createdTime: Long,
    val username: String,
    val token: Token,
) {
    @Transient
    public val expiresInLeft: Long = (createdTime + token.expiresIn) - Clock.System.now().epochSeconds

    @Transient
    public val refreshExpiresInLeft: Long? =
        token.refreshExpiresIn?.let { (createdTime + it) - Clock.System.now().epochSeconds }
}
