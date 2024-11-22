package ai.tech.core.data.crud.model.config

import ai.tech.core.misc.auth.model.AuthResource

public interface CRUDRepositoryConfig {

    public val timeZone: String?
    public val readAuth: AuthResource?
    public val writeAuth: AuthResource?
}
