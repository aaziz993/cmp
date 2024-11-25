package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.Model
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.mobilenativefoundation.store.store5.Fetcher

public abstract class AbstractFetcherFactory<
    Key : Model.Key,
    Properties : Model.Properties,
    Edges : Model.Edges,
    Node : Model.Node<Key, Properties, Edges>,
    O : Output<Model<Key, Properties, Edges>>,
    Op : Operation<Key, Properties, Edges, Node>
    > {

    @Suppress("UNCHECKED_CAST")
    public fun create(): Fetcher<Op, O> =
        Fetcher.ofFlow { operation ->

            require(operation is Operation.Query<*, *, *, *>)

            val mutableSharedFlow = MutableSharedFlow<O>(
                replay = 8,
                extraBufferCapacity = 20,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            when (operation) {
                is Operation.Query.FindOne<*> -> {
                    findAndEmitOne(operation as Operation.Query.FindOne<Key>) { mutableSharedFlow.emit(it) }
                }

                is Operation.Query.FindMany<*> -> {
                    findAndEmitMany(operation as Operation.Query.FindMany<Key>) { mutableSharedFlow.emit(it) }
                }

                is Operation.Query.FindAll -> {
                    findAndEmitAll { mutableSharedFlow.emit(it) }
                }

                is Operation.Query.ObserveOne<*> -> {
                    observeOneAndEmitUpdates(operation as Operation.Query.ObserveOne<Key>) { mutableSharedFlow.emit(it) }
                }

                is Operation.Query.ObserveMany<*> -> {
                    observeManyAndEmitUpdates(operation as Operation.Query.ObserveMany<Key>) { mutableSharedFlow.emit(it) }
                }
            }

            mutableSharedFlow.asSharedFlow()
        }

    private suspend fun findAndEmitOne(
        operation: Operation.Query.FindOne<Key>,
        emit: suspend (O) -> Unit,
    ) {
        val post = client.findOne(operation.key.id)
        emit(Output.Single(post))
    }

    private suspend fun findAndEmitMany(
        operation: Operation.Query.FindMany<Key>,
        emit: suspend (O) -> Unit,
    ) {
        val posts = client.findMany(operation.keys.ids.map { it.id })
        emit(Output.Collection(posts))
    }

    private suspend fun findAndEmitAll(emit: suspend (O) -> Unit) {
        val posts = client.findAll()
        emit(Output.Collection(posts))
    }

    private suspend fun observeOneAndEmitUpdates(
        operation: Operation.Query.ObserveOne<Key>,
        emit: suspend (O) -> Unit,
    ) {
        client.observeOne(operation.key.id).collect { post ->
            emit(Output.Single(post))
        }
    }

    private suspend fun observeManyAndEmitUpdates(
        operation: Operation.Query.ObserveMany<Key>,
        emit: suspend (O) -> Unit,
    ) {
        client.observeMany(operation.keys.map { it.id }).collect { posts ->
            emit(Output.Collection(posts))
        }
    }
}
