package ai.tech.core.data.store5.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(ExperimentalStoreApi::class)
public fun <Key : Any, Output : Any, Response : Any> MutableStore<Key, Output>.request(key: Key, dataSource: DataSource<Key>): Flow<StoreReadResponse<Output>> =
    stream<Response>(dataSource.request(key))
        .filterNot { it is StoreReadResponse.Loading || it is StoreReadResponse.NoNewData }
