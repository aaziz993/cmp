@file:OptIn(ExperimentalStoreApi::class)

package ai.tech.core.data.crud.store5

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.data.crud.store5.model.EntityOutput
import ai.tech.core.data.crud.store5.model.EntityWriteResponse
import ai.tech.core.data.crud.store5.model.EntityFailedSyncEntity
import ai.tech.core.data.crud.store5.model.EntityOperation
import ai.tech.core.data.crud.store5.model.EntityWriteResponse.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Bookkeeper
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult
import org.mobilenativefoundation.store.store5.Validator

public abstract class AbstractCRUDStoreFactory<Network : Any, Local : Any, Domain : Any>(
    private val networkRepository: CRUDRepository<Network>,
    private val localRepository: CRUDRepository<Local>,
    private val bookKeeperRepository: CRUDRepository<EntityFailedSyncEntity>,
    private val disableCache: Boolean? = null,
    private val memoryPolicy: MemoryPolicy<EntityOperation, EntityOutput<Domain>>? = null,
    private val validator: Validator<EntityOutput<Domain>>? = null,
    private val coroutineScope: CoroutineScope,
) {

    protected abstract fun domainToLocal(output: Domain): Local

    protected abstract fun localToDomain(output: Local): Domain

    protected abstract fun domainToNetwork(output: Domain): Network

    protected abstract fun networkToLocal(network: Network): Local

    public fun create(): MutableStore<EntityOperation, EntityOutput<Domain>> =
        MutableStoreBuilder
            .from(
                fetcher = createFetcher(),
                sourceOfTruth = createSourceOfTruth(),
                converter = createConverter(),
            ).cachePolicy(memoryPolicy).apply {
                if (disableCache == true) {
                    disableCache()
                }
                validator?.let(::validator)

                scope(coroutineScope)

            }.build(
                updater = createUpdater(),
                bookkeeper = createBookkeeper(),
            )

    private fun createFetcher(): Fetcher<EntityOperation, EntityOutput<Network>> =
        Fetcher.of { operation ->
            require(operation is EntityOperation.Query)

            networkRepository.handleRead(operation) { it }
        }

    @Suppress("UNCHECKED_CAST")
    private fun createSourceOfTruth(): SourceOfTruth<EntityOperation, EntityOutput<Local>, EntityOutput<Domain>> =
        SourceOfTruth.of(
            reader = { operation ->
                val mutableSharedFlow = MutableSharedFlow<EntityOutput<Domain>?>(
                    replay = 8,
                    extraBufferCapacity = 20,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST,
                )

                require(operation is EntityOperation.Query)

                coroutineScope.launch {
                    mutableSharedFlow.emit(localRepository.handleRead(operation) { it.map(::localToDomain) })
                }

                mutableSharedFlow.asSharedFlow()
            },
            writer = { operation, output ->
                require(operation is EntityOperation.Mutation)

                localRepository.handleWrite(operation, output) { it }
            },
        )

    @Suppress("UNCHECKED_CAST")
    private fun createConverter(): Converter<EntityOutput<Network>, EntityOutput<Local>, EntityOutput<Domain>> =
        Converter
            .Builder<EntityOutput<Network>, EntityOutput<Local>, EntityOutput<Domain>>()
            .fromOutputToLocal { output -> output.map(::domainToLocal) }
            .fromNetworkToLocal { output -> output.map(::networkToLocal) }
            .build()

    private fun createUpdater(): Updater<EntityOperation, EntityOutput<Domain>, EntityWriteResponse<Domain>> =
        Updater.by(
            post = { operation, output ->
                try {
                    UpdaterResult.Success.Typed(networkRepository.handleWrite(operation, output, ::domainToNetwork))
                }
                catch (_: Exception) {
                    UpdaterResult.Error.Message(e.stackTraceToString())
                }
            },
        )

    public fun createBookkeeper(): Bookkeeper<EntityOperation> =
        Bookkeeper.by(
            getLastFailedSync = { operation ->
                // We only check with the bookkeeper on reads
                require(operation is EntityOperation.Query)

                bookKeeperRepository.find(predicate = "operationId".f eq operation.id).firstOrNull()?.timestamp
            },
            setLastFailedSync = { operation, timestamp ->
                // We only set failed syncs on writes
                require(operation is EntityOperation.Mutation)

                try {
                    bookKeeperRepository.insert(EntityFailedSyncEntity(operationId = operation.id, timestamp = timestamp))
                    true
                }
                catch (_: Exception) {
                    false
                }
            },
            clear = { operation ->
                try {
                    bookKeeperRepository.delete("operationId".f eq operation.id)
                    true
                }
                catch (_: Exception) {
                    false
                }
            },
            clearAll = {
                try {
                    bookKeeperRepository.delete()
                    true
                }
                catch (_: Exception) {
                    false
                }
            },
        )

    private suspend fun <T : Any, R : Any> CRUDRepository<T>.handleRead(operation: EntityOperation.Query, transform: (Flow<T>) -> Flow<R>): EntityOutput<R> = with(operation) {
        when (this) {
            is EntityOperation.Query.Find -> if (projections == null) {
                EntityOutput.Typed.Stream(find(sort, predicate, limitOffset).let(transform))
            }
            else {
                EntityOutput.Untyped.Stream(find(projections, sort, predicate, limitOffset))
            }

            is EntityOperation.Query.Aggregate -> EntityOutput.Untyped.Single(aggregate(this.aggregate, this.predicate))
        }
    }

    private suspend fun <T : Any, R : Any> CRUDRepository<R>.handleWrite(operation: EntityOperation, output: EntityOutput<T>, transform: (T) -> R): EntityWriteResponse<R> =
        with(operation) {
            when (this) {
                is EntityOperation.Mutation.Insert -> {
                    require(output is EntityOutput.Typed.Collection<T>)
                    insert(output.values.map(transform))
                    None
                }

                is EntityOperation.Mutation.InsertAndReturn -> {
                    require(output is EntityOutput.Typed.Collection<T>)
                    val values = insertAndReturn(output.values.map(transform))
                    Entities(values)
                }

                is EntityOperation.Mutation.Update -> {
                    require(output is EntityOutput.Typed.Collection<T> || output is EntityOutput.Untyped.Collection)

                    if (output is EntityOutput.Typed.Collection<T>) {
                        val values = update(output.values.map(transform))
                        Update(values)
                    }
                    else {
                        val value = update((output as EntityOutput.Untyped.Collection).values)
                        Count(value)
                    }
                }

                is EntityOperation.Mutation.Upsert -> {
                    require(output is EntityOutput.Typed.Collection<T>)
                    val entities = upsert(output.values.map(transform))
                    Entities(entities)
                }

                is EntityOperation.Mutation.Delete -> {
                    require(output is EntityOutput.Typed.Predicate)
                    val value = delete(output.value)
                    Count(value)
                }

                else -> throw UnsupportedOperationException()
            }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any, R : Any> EntityOutput<T>.map(transform: (T) -> R): EntityOutput<R> = when (this) {
        is EntityOutput.Typed.Collection<*> -> EntityOutput.Typed.Collection((this as EntityOutput.Typed.Collection<T>).values.map(transform))
        is EntityOutput.Typed.Stream<*> -> EntityOutput.Typed.Stream((this as EntityOutput.Typed.Stream<T>).values.map(transform))
        else -> this as EntityOutput<R>
    }
}
