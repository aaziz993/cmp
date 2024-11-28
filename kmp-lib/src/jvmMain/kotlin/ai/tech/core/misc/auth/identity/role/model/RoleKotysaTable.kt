package ai.tech.core.misc.auth.identity.role.model

import ai.tech.core.misc.auth.identity.principal.model.PrincipalKotysaTable
import ai.tech.core.misc.auth.identity.role.model.RoleKotysaTable.identity
import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.columns.KotlinxLocalDateTimeDbTimestampColumnNullable
import org.ufoss.kotysa.columns.LongDbBigIntColumnNotNull
import org.ufoss.kotysa.columns.LongDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNullable
import org.ufoss.kotysa.timestamp

public object RoleKotysaTable : GenericTable<RoleEntity>("role") {

    // Primary key
    public val id: LongDbIdentityColumnNotNull<RoleEntity> = bigInt(RoleEntity::id)
        .identity()
        .primaryKey()

    // Other fields
    public val name: StringDbVarcharColumnNotNull<RoleEntity> = varchar(RoleEntity::name, size = 20).unique()

    public val principalId: LongDbBigIntColumnNotNull<RoleEntity> = bigInt(RoleEntity::principalId)
        .foreignKey(PrincipalKotysaTable.id)

    // metadata
    public val createdBy: StringDbVarcharColumnNullable<RoleEntity> =
        varchar(RoleEntity::createdBy)
    public val createdAt: KotlinxLocalDateTimeDbTimestampColumnNullable<RoleEntity> =
        timestamp(RoleEntity::createdAt)
    public val updatedBy: StringDbVarcharColumnNullable<RoleEntity> =
        varchar(RoleEntity::updatedBy)
    public val updatedAt: KotlinxLocalDateTimeDbTimestampColumnNullable<RoleEntity> =
        timestamp(RoleEntity::updatedAt)
}
