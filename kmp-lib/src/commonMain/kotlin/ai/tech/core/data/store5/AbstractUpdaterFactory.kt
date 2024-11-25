package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.Model
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult

public abstract class AbstractUpdaterFactory<
    Key : Model.Key,
    Properties : Model.Properties,
    Edges : Model.Edges,
    Node : Model.Node<Key, Properties, Edges>,
    O : Output<Model<Key, Properties, Edges>>,
    Op : Operation<Key, Properties, Edges, Node>
    > {

    public fun create(): Updater<Op, O, Any> = Updater.by(
        post = { operation, post ->
            handleOperation(operation, post)
        },
        onCompletion = null,
    )

    private suspend fun handleOperation(operation: Op, post: O): UpdaterResult = when (operation) {
        is Operation.Mutation.Create.InsertOne<*> -> {
            require(post is O.Single && post.value is Post.Properties)
            client.insertOne(post.value as Post.Properties)
        }

        is Operation.Mutation.Create.InsertMany<*> -> {
            require(post is O.Collection && post.items.all { it is Post.Properties })
            client.insertMany(post.items.map { it as Post.Properties })
        }

        is Operation.Mutation.Update.UpdateOne<*, *, *, *> -> {
            require(post is O.Single && post.value is Post.Node)
            client.updateOne(post.value as Post.Node)
        }

        is Operation.Mutation.Update.UpdateMany<*, *, *, *> -> {
            require(post is O.Collection && post.items.all { it is Post.Node })
            client.updateMany(post.items.map { it as Post.Node })
        }

        is Operation.Mutation.Upsert.UpsertOne -> {
            require(post is O.Single && post.value is Post.Properties)
            client.upsertOne(post.value as Post.Properties)
        }

        is Operation.Mutation.Delete.DeleteOne<*> -> {
            client.deleteOne(operation.key)
        }

        is Operation.Mutation.Delete.DeleteMany<*> -> {
            client.deleteMany(operation.keys)
        }

        is Operation.Mutation.Delete.DeleteAll -> {
            client.deleteAll()
        }

        is Operation.Query.FindOne<*> -> {
            require(post is O.Single && post.value is Post.Node)
            client.updateOne(post.value as Post.Node)
        }

        is Operation.Query.FindMany<*> -> {
            require(post is O.Collection && post.items.all { it is Post.Node })
            client.updateMany(post.items.map { it as Post.Node })
        }

        is Operation.Query.FindAll -> {
            require(post is O.Collection && post.items.all { it is Post.Node })
            client.updateMany(post.items.map { it as Post.Node })
        }

        is Operation.Query.ObserveOne<*> -> {
            require(post is O.Single && post.value is Post.Node)
            client.updateOne(post.value as Post.Node)
        }

        is Operation.Query.ObserveMany<*> -> {
            require(post is O.Collection && post.items.all { it is Post.Node })
            client.updateMany(post.items.map { it as Post.Node })
        }

        else -> throw IllegalArgumentException("Unknown operation  \"${operation::class.simpleName}\"")
    }
}
