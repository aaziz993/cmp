package ai.tech.core.data.crud.model.entity

import ai.tech.core.data.crud.model.entity.Entity

public interface EntityWithMetadata<
    ID : Comparable<ID>,
    C : Comparable<C>,
    U : Comparable<U>,
    > : Entity<ID>, CreatedBy, CreatedAt<C>, UpdatedBy, UpdatedAt<U>
