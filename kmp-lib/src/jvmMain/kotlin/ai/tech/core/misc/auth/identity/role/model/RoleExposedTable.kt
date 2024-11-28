package ai.tech.core.misc.auth.identity.role.model

import ai.tech.core.misc.auth.identity.principal.model.PrincipalExposedTable
import ai.tech.core.misc.auth.identity.principal.model.PrincipalKotysaTable.unique
import ai.tech.core.misc.type.serializer.Json
import ai.tech.core.misc.type.serializer.decodeFromAny
import ai.tech.core.misc.type.serializer.encodeAnyToString
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.ufoss.kotysa.columns.StringDbVarcharColumnNullable

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
