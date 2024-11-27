package ai.tech.core.misc.location.model

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

public object LocationExposedTable : LongIdTable("location") {

    // Other fields
    public val longitude: Column<Double> = double("longitude")
    public val latitude: Column<Double> = double("latitude")
    public val altitude: Column<Double> = double("altitude")
    public val identifier: Column<String?> = varchar("identifier", 50).nullable().uniqueIndex()
    public val description: Column<String?> = varchar("description", 200).nullable()

    // metadata
    public val createdBy: Column<String> = varchar("createdBy", 50)
    public val createdAt: Column<LocalDateTime> = datetime("createdAt")
    public val updatedBy: Column<String> = varchar("updatedBy", 50)
    public val updatedAt: Column<LocalDateTime> = datetime("updatedAt")
}
