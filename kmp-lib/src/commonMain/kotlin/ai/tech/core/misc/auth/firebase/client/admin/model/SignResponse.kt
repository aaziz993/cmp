package ai.tech.core.misc.auth.firebase.client.admin.model

import ai.tech.core.misc.auth.model.bearer.BearerToken

public interface SignResponse : BearerToken {

    public val email: String

    public val expiresIn: String
    public val localId: String
}
