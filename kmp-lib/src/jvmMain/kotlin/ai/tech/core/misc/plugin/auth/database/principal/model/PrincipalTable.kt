package ai.tech.core.misc.plugin.auth.database.principal.model

import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.columns.KotlinxLocalDateTimeDbTimestampColumnNullable
import org.ufoss.kotysa.columns.LongDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNullable
import org.ufoss.kotysa.timestamp

public object PrincipalTable : GenericTable<PrincipalEntity>("users") {

    // Primary key
    public val id: LongDbIdentityColumnNotNull<PrincipalEntity> = bigInt(PrincipalEntity::id)
        .identity()
        .primaryKey("PK_users")

    // Other fields
    public val username: StringDbVarcharColumnNotNull<PrincipalEntity> = varchar(PrincipalEntity::username)
    public val password: StringDbVarcharColumnNotNull<PrincipalEntity> = varchar(PrincipalEntity::password)

    // metadata
    public val createdBy: StringDbVarcharColumnNullable<PrincipalEntity> =
        varchar(PrincipalEntity::createdBy)
    public val createdAt: KotlinxLocalDateTimeDbTimestampColumnNullable<PrincipalEntity> =
        timestamp(PrincipalEntity::createdAt)
    public val updatedBy: StringDbVarcharColumnNullable<PrincipalEntity> =
        varchar(PrincipalEntity::updatedBy)
    public val updatedAt: KotlinxLocalDateTimeDbTimestampColumnNullable<PrincipalEntity> =
        timestamp(PrincipalEntity::updatedAt)
}
