package ai.tech.core.data.crud.model.config

import ai.tech.core.misc.auth.model.AuthResource
import kotlinx.datetime.TimeZone

public interface CRUDRepositoryConfig {

    public val transactionIsolation: Int?
    public val statementCount: Int?
    public val duration: Long?
    public val warnLongQueriesDuration: Long?
    public val debug: Boolean?
    public val maxAttempts: Int?
    public val minRetryDelay: Long?
    public val maxRetryDelay: Long?
    public val queryTimeout: Int?
    public val timeZone: TimeZone
    public val readAuth: AuthResource?
    public val writeAuth: AuthResource?
}
