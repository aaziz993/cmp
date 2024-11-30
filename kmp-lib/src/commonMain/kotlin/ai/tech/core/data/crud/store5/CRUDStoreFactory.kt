@file:OptIn(ExperimentalStoreApi::class)

package ai.tech.core.data.crud.store5

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.store5.model.EntityFailedSync
import ai.tech.core.data.crud.store5.model.EntityOperation
import ai.tech.core.data.crud.store5.model.EntityOutput
import ai.tech.core.data.crud.store5.model.EntityWriteResponse
import ai.tech.core.data.crud.store5.model.EntityWriteResponse.Count
import ai.tech.core.data.crud.store5.model.EntityWriteResponse.Entities
import ai.tech.core.data.crud.store5.model.EntityWriteResponse.None
import ai.tech.core.data.crud.store5.model.EntityWriteResponse.Update
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

public class CRUDStoreFactory<Network : Any, Local : Any, Domain : Any>(
    private val networkRepository: CRUDRepository<Network>,
    private val localRepository: CRUDRepository<Local>,
    private val bookKeeperRepository: CRUDRepository<EntityFailedSync<*>>,
    private val networkToLocal: (Network) -> Local,
    private val localToDomain: (Local) -> Domain,
    private val domainToLocal: (Domain) -> Local,
    private val domainToNetwork: (Domain) -> Network,
    private val disableCache: Boolean? = null,
    private val memoryPolicy: MemoryPolicy<EntityOperation, EntityOutput<Domain>>? = null,
    private val validator: Validator<EntityOutput<Domain>>? = null,
    private val coroutineScope: CoroutineScope,
) {

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

    // Using Fetcher.ofFlow allows us to support operations that observe changes over time, such as ObserveOne and ObserveMany.
    private fun createFetcher(): Fetcher<EntityOperation, EntityOutput<Network>> =
        Fetcher.ofFlow { operation ->
            require(operation is EntityOperation.Query)

            val mutableSharedFlow = MutableSharedFlow<EntityOutput<Network>>(
                replay = 8,
                extraBufferCapacity = 20,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            coroutineScope.launch {
                mutableSharedFlow.emit(networkRepository.handleRead(operation) { it })
            }

            mutableSharedFlow.asSharedFlow()
        }

    @Suppress("UNCHECKED_CAST")
    private fun createSourceOfTruth(): SourceOfTruth<EntityOperation, EntityOutput<Local>, EntityOutput<Domain>> =
        SourceOfTruth.of(
            // When emitting data, if there is no value to emit (e.g., no entities found), we return null instead of an empty list.
            // This prevents the Store from considering the operation fulfilled and triggers a fetch from the network.
            reader = { operation ->

                val mutableSharedFlow = MutableSharedFlow<EntityOutput<Domain>?>(
                    replay = 8,
                    extraBufferCapacity = 20,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST,
                )

                require(operation is EntityOperation.Query)

                coroutineScope.launch {
                    mutableSharedFlow.emit(localRepository.handleRead(operation) { it.map(localToDomain) })
                }

                mutableSharedFlow.asSharedFlow()
            },
            // However, our Writer needs to handle all Query and Mutation operations because we write to the Source of Truth on both reads and writes.
            // Ensure that Writer handles all operation cases, including both mutations and queries, to maintain consistency between the local data and remote sources.
            writer = { operation, output ->
                require(operation is EntityOperation.Mutation)

                localRepository.handleWrite(operation, output) { it }
            },
        )

    @Suppress("UNCHECKED_CAST")
    private fun createConverter(): Converter<EntityOutput<Network>, EntityOutput<Local>, EntityOutput<Domain>> =
        Converter
            .Builder<EntityOutput<Network>, EntityOutput<Local>, EntityOutput<Domain>>()
            .fromOutputToLocal { output -> output.map(domainToLocal) }
            .fromNetworkToLocal { output -> output.map(networkToLocal) }
            .build()

    // We never invoke fetcher after local writes
    // We invoke Updater on reads if conflicts might exist.
    // This means we need to handle query operations too.
    // If we do hit code for handling a query operation, it means we are fetching a Post but the Bookkeeper has an unresolved sync failure for that Post.
    // So, before we can pull the latest value, we need to push our latest local value to the network.
    private fun createUpdater(): Updater<EntityOperation, EntityOutput<Domain>, EntityWriteResponse<Domain>> =
        Updater.by(
            post = { operation, output ->
                try {
                    if (operation is EntityOperation.Query) {
                        UpdaterResult.Success.Typed(0)
                    }
                    else {
                        UpdaterResult.Success.Typed(networkRepository.handleWrite(operation, output, domainToNetwork))
                    }
                }
                catch (e: Exception) {
                    UpdaterResult.Error.Message(e.stackTraceToString())
                }
            },
        )

    public fun createBookkeeper(): Bookkeeper<EntityOperation> =
        Bookkeeper.by(
            getLastFailedSync = { operation ->
                // We only check with the bookkeeper on reads
                require(operation is EntityOperation.Query)

                null
            },
            setLastFailedSync = { operation, timestamp ->
                // We only set failed syncs on writes
                require(operation is EntityOperation.Mutation)

                try {

                    true
                }
                catch (_: Exception) {
                    false
                }
            },
            clear = { operation ->
                try {
                    when (operation) {
                        is EntityOperation.Query.Find -> clearFailedSyncs(operation.predicate)
                        is EntityOperation.Mutation.Delete -> clearFailedSyncs(operation.predicate)

                    }
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

    private suspend fun clearFailedSyncs(predicate: BooleanVariable?) {
        bookKeeperRepository.delete(("predicate".f eq predicate?.id).and(("sync".f eq "INSERT").or("sync".f eq "UPDATE").or("sync".f eq "DELETE")))
    }

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

    private suspend fun <T : Any, R : Any> CRUDRepository<R>.handleWrite(operation: EntityOperation.Mutation, output: EntityOutput<T>, transform: (T) -> R): EntityWriteResponse<R> =
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
                    val value = delete(predicate)
                    Count(value)
                }
            }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any, R : Any> EntityOutput<T>.map(transform: (T) -> R): EntityOutput<R> = when (this) {
        is EntityOutput.Typed.Collection<*> -> EntityOutput.Typed.Collection((this as EntityOutput.Typed.Collection<T>).values.map(transform))
        is EntityOutput.Typed.Stream<*> -> EntityOutput.Typed.Stream((this as EntityOutput.Typed.Stream<T>).values.map(transform))
        else -> this as EntityOutput<R>
    }
}

private val BooleanVariable.id: String
    get() = Json.Default.encodeToString(this)