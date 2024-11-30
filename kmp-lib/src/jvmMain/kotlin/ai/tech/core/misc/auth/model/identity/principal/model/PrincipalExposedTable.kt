package ai.tech.core.misc.auth.model.identity.principal.model

import ai.tech.core.data.database.exposed.transform.anyJson
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

public object PrincipalExposedTable : LongIdTable("principal") {

    // Other fields
    public val username: Column<String> = varchar("username", 20).uniqueIndex()
    public val password: Column<String> = varchar("password", 20)
    public val firstName: Column<String?> = varchar("firstName", 20).nullable()
    public val lastName: Column<String?> = varchar("lastName", 20).nullable()
    public val phone: Column<String?> = varchar("phone", 20).nullable()
    public val email: Column<String?> = varchar("email", 20).nullable()
    public val image: Column<String?> = varchar("image", 200).nullable()
    public val attributes: Column<Map<String, Any?>> = anyJson("attributes")

    // metadata
    public val createdBy: Column<String> = varchar("createdBy", 50)
    public val createdAt: Column<LocalDateTime> = datetime("createdAt")
    public val updatedBy: Column<String> = varchar("updatedBy", 50)
    public val updatedAt: Column<LocalDateTime> = datetime("updatedAt")
}
