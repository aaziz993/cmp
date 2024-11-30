@file:OptIn(ExperimentalStoreApi::class)

package ai.tech.core.data.crud.store5

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.data.crud.store5.model.DataSource
import ai.tech.core.data.crud.store5.model.EntityOperation
import ai.tech.core.data.crud.store5.model.EntityOutput
import ai.tech.core.data.crud.store5.model.EntityWriteResponse
import ai.tech.core.data.crud.store5.model.isLocalOnly
import ai.tech.core.data.crud.store5.model.isRemoteOnly
import ai.tech.core.data.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest
import org.mobilenativefoundation.store.store5.StoreWriteResponse

public class StoreCRUDRepository<Domain : Any>(
    private val store: MutableStore<EntityOperation, EntityOutput<Domain>>,
    public val dataSource: DataSource = DataSource.all,
) : CRUDRepository<Domain> {

    override suspend fun <R> transactional(block: suspend CRUDRepository<Domain>.(Transaction) -> R): R = throw UnsupportedOperationException()

    override suspend fun insert(entities: List<Domain>) {
        handleWrite(EntityOperation.Mutation.Insert, EntityOutput.Typed.Collection(entities))
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun insertAndReturn(entities: List<Domain>): List<Domain> =
        (handleWrite(EntityOperation.Mutation.InsertAndReturn, EntityOutput.Typed.Collection(entities))
            as? EntityWriteResponse.Entities<Domain>)?.values.orEmpty()

    override suspend fun update(entities: List<Domain>): List<Boolean> = (handleWrite(EntityOperation.Mutation.Update, EntityOutput.Typed.Collection(entities))
        as? EntityWriteResponse.Update)?.values.orEmpty()

    override suspend fun update(entities: List<Map<String, Any?>>, predicate: BooleanVariable?): Long =
        (handleWrite(EntityOperation.Mutation.Update, EntityOutput.Untyped.Collection(entities))
            as? EntityWriteResponse.Count)?.value ?: 0

    @Suppress("UNCHECKED_CAST")
    override suspend fun upsert(entities: List<Domain>): List<Domain> =
        (handleWrite(EntityOperation.Mutation.Upsert, EntityOutput.Typed.Collection(entities))
            as? EntityWriteResponse.Entities<Domain>)?.values.orEmpty()

    @OptIn(ExperimentalStoreApi::class)
    override fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<Domain> = flow {
        store.handleRead(EntityOperation.Query.Find(null, sort, predicate, limitOffset, dataSource))?.let { output ->
            require(output is EntityOutput.Typed.Stream<Domain>)

            emitAll(output.values)
        }
    }

    override fun find(projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<List<Any?>> =
        flow {
            store.handleRead(EntityOperation.Query.Find(projections, sort, predicate, limitOffset, dataSource))?.let { output ->
                require(output is EntityOutput.Untyped.Stream)

                emitAll(output.values)
            }
        }

    override suspend fun delete(predicate: BooleanVariable?): Long = (handleWrite(EntityOperation.Mutation.Delete(predicate), EntityOutput.Untyped.Single(null))
        as? EntityWriteResponse.Count)?.value ?: 0

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T =
        store.handleRead(EntityOperation.Query.Aggregate(aggregate, predicate, dataSource))?.let { output ->
            require(output is EntityOutput.Untyped.Single)

            output.value
        } as T

    private suspend fun MutableStore<EntityOperation, EntityOutput<Domain>>.handleRead(input: EntityOperation.Query): EntityOutput<Domain>? {

        val request = when {
            dataSource.isRemoteOnly() -> StoreReadRequest.fresh(input)
            dataSource.isLocalOnly() -> StoreReadRequest.localOnly(input)
            else -> StoreReadRequest.fresh(input)
        }

        return store.stream<EntityWriteResponse<Domain>>(request)
            .firstOrNull { it is StoreReadResponse.Data }?.dataOrNull()
    }

    private suspend fun handleWrite(operation: EntityOperation.Mutation, output: EntityOutput<Domain>): Any? {
        val request = StoreWriteRequest.of<EntityOperation, EntityOutput<Domain>, EntityWriteResponse<Domain>>(operation, output)

        return when (val response = store.write(request)) {
            is StoreWriteResponse.Error.Exception -> null
            is StoreWriteResponse.Error.Message -> null
            is StoreWriteResponse.Success.Typed<*> -> response.value

            is StoreWriteResponse.Success.Untyped -> response.value
        }
    }
}
