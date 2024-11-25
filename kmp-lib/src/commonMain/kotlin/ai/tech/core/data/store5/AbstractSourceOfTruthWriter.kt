package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.Model

public abstract class AbstractSourceOfTruthWriter<
    Key : Model.Key,
    Properties : Model.Properties,
    Edges : Model.Edges,
    Node : Model.Node<Key, Properties, Edges>,
    O : Output<Model<Key, Properties, Edges>>,
    Op : Operation<Key, Properties, Edges, Node>
    > {

    @Suppress("UNCHECKED_CAST")
    public suspend fun handleWrite(
        operation: Op,
        value: O? = null,
    ) {
        when (operation) {
            is Operation.Mutation.Create<*, *, *> -> handleCreate(operation as Operation.Mutation.Create<Key, Properties, Edges>, value!!)
            is Operation.Query<*, *, *, *> -> handleQuery(operation as Operation.Query<Key, Properties, Edges, Node>, value!!)
            is Operation.Mutation.Update<*, *, *> -> handleUpdate(operation as Operation.Mutation.Update<Key, Properties, Edges>, value!!)
            is Operation.Mutation.Delete<*> -> handleDelete(operation as Operation.Mutation.Delete<Key>)
        }
    }

    private suspend fun handleCreate(
        operation: Operation.Mutation.Create<Key, Properties, Edges>,
        value: O,
    ) {
        when (operation) {
            is Operation.Mutation.Create.InsertOne -> {
                trailsDatabase.postQueries.insertPost(value.item)
            }

            is Operation.Mutation.Create.InsertMany -> {
                value.items.forEach { trailsDatabase.postQueries.insertPost(it) }
            }
        }
    }

    private suspend fun handleQuery(
        operation: Operation.Query<Key, Properties, Edges, Node>,
        value: O,
    ) {
        when (operation) {
            is Operation.Query.FindOne -> {
                trailsDatabase.postQueries.insertPostOrIgnore(value.item)
            }

            is Operation.Query.FindMany -> {
                value.items.forEach { trailsDatabase.postQueries.insertPostOrIgnore(it) }
            }

            is Operation.Query.FindAll -> {
                value.items.forEach { trailsDatabase.postQueries.insertPostOrIgnore(it) }
            }

            is Operation.Query.ObserveOne -> {
                trailsDatabase.postQueries.insertPostOrIgnore(value.item)
            }

            is Operation.Query.ObserveMany -> {
                value.items.forEach { trailsDatabase.postQueries.insertPostOrIgnore(it) }
            }
        }
    }

    private suspend fun handleUpdate(
        operation: Operation.Mutation.Update<Key, Properties, Edges>,
        value: O,
    ) {
        when (operation) {
            is Operation.Mutation.Update.UpdateOne<*, *, *, *> -> {
                trailsDatabase.postQueries.updatePost(value.item)
            }

            is Operation.Mutation.Update.UpdateMany<*, *, *, *> -> {
                value.items.forEach { trailsDatabase.postQueries.updatePost(it) }
            }

            else -> Unit
        }
    }

    private suspend fun handleDelete(operation: Operation.Mutation.Delete<Key>) {
        when (operation) {
            is Operation.Mutation.Delete.DeleteOne -> {
                trailsDatabase.postQueries.deletePostById(operation.key.id.toLong())
            }

            is Operation.Mutation.Delete.DeleteMany -> {
                operation.keys.forEach { trailsDatabase.postQueries.deletePostById(it.id.toLong()) }
            }

            is Operation.Mutation.Delete.DeleteAll -> {
                trailsDatabase.postQueries.deleteAllPosts()
            }
        }
    }
}
