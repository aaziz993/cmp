package ai.tech.core.misc.auth.model.identity.role.model

import ai.tech.core.misc.auth.model.identity.principal.model.PrincipalExposedTable
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

public object RoleExposedTable : LongIdTable("role") {

    // Other fields
    public val name: Column<String> = varchar("name", 20).uniqueIndex()
    public val principalId: Column<EntityID<Long>> = reference("principalId", PrincipalExposedTable)

    // metadata
    public val createdBy: Column<String> = varchar("createdBy", 50)
    public val createdAt: Column<LocalDateTime> = datetime("createdAt")
    public val updatedBy: Column<String> = varchar("updatedBy", 50)
    public val updatedAt: Column<LocalDateTime> = datetime("updatedAt")
}
