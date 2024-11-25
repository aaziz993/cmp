package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.Model
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

public abstract class AbstractPostSourceOfTruthReader<
    Key : Model.Key,
    Properties : Model.Properties,
    Edges : Model.Edges,
    Node : Model.Node<Key, Properties, Edges>,
    O : Output<Model<Key, Properties, Edges>>
    >(private val coroutineScope: CoroutineScope) {

    public fun handleRead(
        operation: Operation.Query<Key, Properties, Edges, Node>,
        emit: suspend (O?) -> Unit,
    ) {
        when (operation) {
            is Operation.Query.FindOne -> findOne(operation, emit)
            is Operation.Query.FindAll -> findAll(emit)
            is Operation.Query.FindMany -> findMany(operation, emit)
            is Operation.Query.ObserveOne -> observeOne(operation, emit)
            is Operation.Query.ObserveMany -> observeMany(operation, emit)
        }
    }

    private fun findOne(
        operation: Operation.Query.FindOne<Key>,
        emit: suspend (O?) -> Unit,
    ) = coroutineScope.launch {
        val entity =
            trailsDatabase
                .postQueries
                .selectPostById(operation.key.id.toLong())
                .executeAsOneOrNull()
        val output = entity?.asNode()?.let { Output.Single(it) }
        emit(output)
    }

    private fun findMany(
        operation: Operation.Query.FindMany<Key>,
        emit: suspend (O?) -> Unit,
    ) {
        coroutineScope.launch {
            val entities =
                trailsDatabase
                    .postQueries
                    .selectPostsByIds(operation.keys.map {}.map { it.toLong() })
                    .executeAsList()

            val output =
                if (entities.isEmpty()) {
                    null
                }
                else {
                    Output.Collection(entities.map { it.asNode() })
                }
            emit(output)
        }
    }

    private fun findAll(emit: suspend (O?) -> Unit) {
        coroutineScope.launch {
            val entities = trailsDatabase.postQueries.selectAllPosts().executeAsList()

            val output =
                if (entities.isEmpty()) {
                    null
                }
                else {
                    Output.Collection(entities.map { it.asNode() })
                }
            emit(output)
        }
    }

    private fun observeOne(
        operation: Operation.Query.ObserveOne<Key>,
        emit: suspend (O?) -> Unit,
    ) {
        coroutineScope.launch {
            trailsDatabase.postQueries.selectPostById(operation.key.id.toLong()).asFlow().collect { query ->
                val entity = query.executeAsOneOrNull()
                val output = entity?.asNode()?.let { Output.Single(it) }
                emit(output)
            }
        }
    }

    private fun observeMany(
        operation: Operation.Query.ObserveMany<Key>,
        emit: suspend (O?) -> Unit,
    ) {
        coroutineScope.launch {
            trailsDatabase
                .postQueries
                .selectPostsByIds(operation.keys.map {}.map { it.toLong() })
                .asFlow()
                .collect { query ->
                    val entities = query.executeAsList()

                    val output =
                        if (entities.isEmpty()) {
                            null
                        }
                        else {
                            Output.Collection(entities.map { it.asNode() })
                        }
                    emit(output)
                }
        }
    }
}
