package ai.tech.core.misc.auth.identity.principal.model

import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.columns.KotlinxLocalDateTimeDbTimestampColumnNullable
import org.ufoss.kotysa.columns.LongDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNullable
import org.ufoss.kotysa.timestamp

public object PrincipalKotysaTable : GenericTable<PrincipalEntity>("principal") {

    // Primary key
    public val id: LongDbIdentityColumnNotNull<PrincipalEntity> = bigInt(PrincipalEntity::id)
        .identity()
        .primaryKey()

    // Other fields
    public val username: StringDbVarcharColumnNotNull<PrincipalEntity> = varchar(PrincipalEntity::username, size = 20).unique()
    public val password: StringDbVarcharColumnNotNull<PrincipalEntity> = varchar(PrincipalEntity::password, size = 20)
    public val firstName: StringDbVarcharColumnNullable<PrincipalEntity> = varchar(PrincipalEntity::firstName, size = 20)
    public val lastName: StringDbVarcharColumnNullable<PrincipalEntity> = varchar(PrincipalEntity::lastName, size = 20)
    public val phone: StringDbVarcharColumnNullable<PrincipalEntity> = varchar(PrincipalEntity::phone, size = 20)
    public val email: StringDbVarcharColumnNullable<PrincipalEntity> = varchar(PrincipalEntity::email, size = 20)
    public val image: StringDbVarcharColumnNullable<PrincipalEntity> = varchar(PrincipalEntity::image, size = 200)
//    public val attributes =

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
