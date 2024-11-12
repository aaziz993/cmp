package ai.tech.core.misc.plugin.auth.database.role.model

import ai.tech.core.misc.plugin.auth.database.principal.model.PrincipalTable
import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.columns.KotlinxLocalDateTimeDbTimestampColumnNullable
import org.ufoss.kotysa.columns.LongDbBigIntColumnNotNull
import org.ufoss.kotysa.columns.LongDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNotNull
import org.ufoss.kotysa.columns.StringDbVarcharColumnNullable
import org.ufoss.kotysa.timestamp

public object RoleTable : GenericTable<RoleEntity>("roles") {

    // Primary key
    public val id: LongDbIdentityColumnNotNull<RoleEntity> = bigInt(RoleEntity::id)
        .identity()
        .primaryKey("PK_roles")

    // Other fields
    public val name: StringDbVarcharColumnNotNull<RoleEntity> = varchar(RoleEntity::name)

    // Foreign keys
    public val mapperId: LongDbBigIntColumnNotNull<RoleEntity> = bigInt(RoleEntity::userId)
        .foreignKey(PrincipalTable.id, "FK_role_user")

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
