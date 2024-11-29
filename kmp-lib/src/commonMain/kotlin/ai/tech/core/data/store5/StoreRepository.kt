package ai.tech.core.data.store5

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreWriteRequest
import org.mobilenativefoundation.store.store5.impl.extensions.fresh

public class StoreRepository<Domain : Any> @OptIn(ExperimentalStoreApi::class) constructor(
    private val store: MutableStore<Operation, Domain>
) : CRUDRepository<Domain> {

    override suspend fun getPost(id: Int): Post? {
        return postStore.fresh<Int, Post, Boolean>(id)
    }

    override suspend fun updatePost(
        postId: Int,
        likesCount: Long?,
        commentsCount: Long?,
        sharesCount: Long?,
        viewsCount: Long?,
        isFavoritedByCurrentUser: Boolean?
    ): Post {
        val prevPost = postStore.get<Int, Post, Boolean>(postId)

        val nextPost = prevPost.copy(
            likesCount = likesCount ?: prevPost.likesCount,
            commentsCount = commentsCount ?: prevPost.commentsCount,
            sharesCount = sharesCount ?: prevPost.sharesCount,
            viewsCount = viewsCount ?: prevPost.viewsCount,
            isFavoritedByCurrentUser = isFavoritedByCurrentUser ?: prevPost.isFavoritedByCurrentUser,
        )

        val writeRequest = StoreWriteRequest.of<Int, Post, Boolean>(
            key = postId,
            value = nextPost,
        )

        return when (store.write(writeRequest)) {
            is StoreWriteResponse.Error -> prevPost
            is StoreWriteResponse.Success -> nextPost
        }
    }

    override suspend fun <R> transactional(block: suspend CRUDRepository<Domain>.() -> R): R = throw UnsupportedOperationException()

    @OptIn(ExperimentalStoreApi::class)
    override suspend fun insert(entities: List<Domain>) {
        store.stream<Domain>(entities.map { StoreWriteRequest.of<Operation, Domain, Domain>(Operation.Insert, it) }.asFlow())
    }

    override suspend fun update(entities: List<Domain>): List<Boolean> =
        store.stream<Domain>(entities.map { StoreWriteRequest.of<Operation, Domain, Boolean>(Operation.Update, it) }.asFlow())

    override suspend fun update(entities: List<Map<String, Any?>>, predicate: BooleanVariable?): List<Long> {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalStoreApi::class)
    override fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<Domain> =
        store.stream<Domain>(StoreReadRequest.fresh(Operation.Find(sort, predicate, limitOffset))).map { it.requireData() }

    override fun find(projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<List<Any?>> =
        throw UnsupportedOperationException()

    override suspend fun delete(predicate: BooleanVariable?): Long {
        StoreWriteRequest.of()
    }

    override suspend fun <T : Comparable<T>> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T {
        TODO("Not yet implemented")
    }
}
