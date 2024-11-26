package ai.tech.core.data.store5

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.data.store5.model.BookKeeperEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Bookkeeper
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult

public abstract class AbstractStoreFactory<Network : Any, Local : Any, Domain : Any>(
    private val networkRepository: CRUDRepository<Network>,
    private val localRepository: CRUDRepository<Local>,
    private val bookKeeperRepository: CRUDRepository<BookKeeperEntity>,
    private val coroutineScope: CoroutineScope,
) {

    protected abstract fun domainToLocal(output: Domain): Local

    protected abstract fun localToDomain(output: Local): Domain

    protected abstract fun domainToNetwork(output: Domain): Network

    protected abstract fun networkToLocal(network: Network): Local

    @OptIn(ExperimentalStoreApi::class)
    public fun create(): MutableStore<Operation, Domain> =
        MutableStoreBuilder
            .from(
                fetcher = createFetcher(),
                sourceOfTruth = createSourceOfTruth(),
                converter = createConverter(),
            ).build(
                updater = createUpdater(),
                bookkeeper = createBookkeeper(),
            )

    private fun createFetcher(): Fetcher<Operation, Network> =
        Fetcher.ofFlow { operation ->
            require(operation is Operation.Find)

            networkRepository.find(operation.sort, operation.predicate, operation.limitOffset)
        }

    @Suppress("UNCHECKED_CAST")
    private fun createSourceOfTruth(): SourceOfTruth<Operation, Local, Domain> =
        SourceOfTruth.of(
            reader = { operation ->
                require(operation is Operation.Find)

                localRepository.find(operation.sort, operation.predicate, operation.limitOffset).map(::localToDomain)
            },
            writer = { operation, entity ->
                when (operation) {
                    Operation.Insert -> localRepository.insert(entity)
                    Operation.Update -> localRepository.update(entity)
                    else -> Unit
                }
            },
            delete = { operation ->
                require(operation is Operation.Delete)

                coroutineScope.launch {
                    localRepository.delete(operation.predicate)
                }
            },
            deleteAll = {
                coroutineScope.launch {
                    localRepository.delete()
                }
            },
        )

    private fun createConverter(): Converter<Network, Local, Domain> =
        Converter
            .Builder<Network, Local, Domain>()
            .fromOutputToLocal(::domainToLocal)
            .fromNetworkToLocal(::networkToLocal)
            .build()

    private fun createUpdater(): Updater<Operation, Domain, Long> =
        Updater.by(post = { _, entity -> UpdaterResult.Success.Typed(networkRepository.update(domainToNetwork(entity))[0]) })

    public fun createBookkeeper(): Bookkeeper<Operation> =
        Bookkeeper.by(
            getLastFailedSync = { operation ->
                bookKeeperRepository.find(predicate = "operationId".f eq operation.id).firstOrNull()?.timestamp
            },
            setLastFailedSync = { operation, timestamp ->
                bookKeeperRepository.insert(BookKeeperEntity(operationId = operation.id, timestamp = timestamp))
                true
            },
            clear = { operation ->
                bookKeeperRepository.delete("operationId".f eq operation.id) > 0
            },
            clearAll = {
                bookKeeperRepository.delete() > 0
            },
        )
}
