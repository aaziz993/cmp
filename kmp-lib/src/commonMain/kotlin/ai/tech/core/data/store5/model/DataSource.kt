package ai.tech.core.data.store5.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest

public sealed interface DataSource<Key : Any> {

    public fun request(key: Key): StoreReadRequest<Key>

    public data class Fresh<Key : Any>(val fallBackToSourceOfTruth: Boolean = false) : DataSource<Key> {

        override fun request(key: Key): StoreReadRequest<Key> = StoreReadRequest.fresh(key, fallBackToSourceOfTruth)
    }

    public data class Cached<Key : Any>(val refresh: Boolean) : DataSource<Key> {

        override fun request(key: Key): StoreReadRequest<Key> = StoreReadRequest.cached(key, dataSource.refresh)
    }

    public class LocalOnly<Key : Any> : DataSource<Key> {

        override fun request(key: Key): StoreReadRequest<Key> = StoreReadRequest.localOnly(key)
    }

    public data class SkipMemory<Key : Any>(val refresh: Boolean) : DataSource<Key> {

        override fun request(key: Key): StoreReadRequest<Key> = StoreReadRequest.skipMemory(key, refresh)
    }
}
