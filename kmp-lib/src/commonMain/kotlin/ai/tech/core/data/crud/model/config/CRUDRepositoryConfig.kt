package ai.tech.core.data.crud.model.config

import ai.tech.core.misc.auth.model.AuthResource
import kotlinx.datetime.TimeZone

public interface CRUDRepositoryConfig {

    public val timeZone: TimeZone
    public val readAuth: AuthResource?
    public val writeAuth: AuthResource?
}
