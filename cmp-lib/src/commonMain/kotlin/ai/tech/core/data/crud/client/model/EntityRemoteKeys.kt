package ai.tech.core.data.crud.client.model

import ai.tech.core.data.paging.model.RemoteKeys

public interface EntityRemoteKeys<ID : Any> : RemoteKeys<Long> {

    public val entityId: ID
}
