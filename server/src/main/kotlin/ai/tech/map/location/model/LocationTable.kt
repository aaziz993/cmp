package ai.tech.map.location.model

import ai.tech.core.misc.location.model.LocationEntity
import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.columns.DoubleDbDoublePrecisionColumnNotNull
import org.ufoss.kotysa.columns.KotlinxLocalDateTimeDbTimestampColumnNullable
import org.ufoss.kotysa.columns.LongDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNullable

public object LocationTable : GenericTable<LocationEntity>("location") {
    // Primary key
    public val id: LongDbIdentityColumnNotNull<LocationEntity> = bigInt(LocationEntity::id)
        .identity()
        .primaryKey("PK_location")

    // Other fields
    public val longitude: DoubleDbDoublePrecisionColumnNotNull<LocationEntity> =
        doublePrecision(LocationEntity::longitude)
    public val latitude: DoubleDbDoublePrecisionColumnNotNull<LocationEntity> =
        doublePrecision(LocationEntity::latitude)
    public val altitude: DoubleDbDoublePrecisionColumnNotNull<LocationEntity> =
        doublePrecision(LocationEntity::altitude)
    public val identifier: StringDbVarcharColumnNullable<LocationEntity> = varchar(LocationEntity::identifier).unique()
    public val description: StringDbVarcharColumnNullable<LocationEntity> = varchar(LocationEntity::description)

    // metadata
    public val createdBy: StringDbVarcharColumnNullable<LocationEntity> =
        varchar(LocationEntity::createdBy)
    public val createdAt: KotlinxLocalDateTimeDbTimestampColumnNullable<LocationEntity> =
        timestamp(LocationEntity::createdAt)
    public val updatedBy: StringDbVarcharColumnNullable<LocationEntity> =
        varchar(LocationEntity::updatedBy)
    public val updatedAt: KotlinxLocalDateTimeDbTimestampColumnNullable<LocationEntity> =
        timestamp(LocationEntity::updatedAt)
}
