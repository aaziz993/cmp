package ai.tech.core.misc.location.model

import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.columns.DoubleDbDoublePrecisionColumnNotNull
import org.ufoss.kotysa.columns.KotlinxLocalDateTimeDbTimestampColumnNullable
import org.ufoss.kotysa.columns.LongDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNullable
import org.ufoss.kotysa.timestamp

public object LocationTable : GenericTable<GeolocationEntity>("location") {

    // Primary key
    public val id: LongDbIdentityColumnNotNull<GeolocationEntity> = bigInt(GeolocationEntity::id)
        .identity()
        .primaryKey("PK_location")

    // Other fields
    public val longitude: DoubleDbDoublePrecisionColumnNotNull<GeolocationEntity> =
        doublePrecision(GeolocationEntity::longitude)
    public val latitude: DoubleDbDoublePrecisionColumnNotNull<GeolocationEntity> =
        doublePrecision(GeolocationEntity::latitude)
    public val altitude: DoubleDbDoublePrecisionColumnNotNull<GeolocationEntity> =
        doublePrecision(GeolocationEntity::altitude)
    public val identifier: StringDbVarcharColumnNullable<GeolocationEntity> = varchar(GeolocationEntity::identifier).unique()
    public val description: StringDbVarcharColumnNullable<GeolocationEntity> = varchar(GeolocationEntity::description)

    // metadata
    public val createdBy: StringDbVarcharColumnNullable<GeolocationEntity> =
        varchar(GeolocationEntity::createdBy)
    public val createdAt: KotlinxLocalDateTimeDbTimestampColumnNullable<GeolocationEntity> =
        timestamp(GeolocationEntity::createdAt)
    public val updatedBy: StringDbVarcharColumnNullable<GeolocationEntity> =
        varchar(GeolocationEntity::updatedBy)
    public val updatedAt: KotlinxLocalDateTimeDbTimestampColumnNullable<GeolocationEntity> =
        timestamp(GeolocationEntity::updatedAt)
}
