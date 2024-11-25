package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.Model
import org.mobilenativefoundation.store.store5.Bookkeeper

public abstract class AbstractBookkeeperFactory<
    Key : Model.Key,
    Properties : Model.Properties,
    Edges : Model.Edges,
    Node : Model.Node<Key, Properties, Edges>,
    Op : Operation<Key, Properties, Edges, Node>
    > {
    public fun create(): Bookkeeper<Op> =
        Bookkeeper.by(
            getLastFailedSync = { operation ->
                require(operation is Operation.Query<*, *, *, *>)

                handleGetLastFailedSync(operation)
            },
            setLastFailedSync = { operation, timestamp ->
                require(operation is Operation.Mutation<*, *, *, *>)
                handleSetLastFailedSync(operation, timestamp)
            },
            clear = { operation ->
                handleClear(operation)
            },
            clearAll = {
                trailsDatabase.postBookkeepingQueries.clearAllFailedSyncs()
                true
            },
        )

    private fun handleGetLastFailedSync(operation: Operation.Query<Key, Properties, Edges, Node>): Long? {
        return when (operation) {
            is Operation.Query.FindOne -> {
                return firstFailedSyncOrNull(operation.key.id.toLong())
            }

            is Operation.Query.FindMany -> {
                return operation.keys.firstOrNull { firstFailedSyncOrNull(it.id.toLong()) }
            }

            is Operation.Query.FindAll -> {
                val failedUpdates = trailsDatabase.postBookkeepingQueries.getFailedUpdates().executeAsList()
                val failedDeletes = trailsDatabase.postBookkeepingQueries.getFailedDeletes().executeAsList()

                return when {
                    failedUpdates.isEmpty() && failedDeletes.isEmpty() -> null
                    failedUpdates.isNotEmpty() -> failedUpdates.first().timestamp
                    else -> failedDeletes.first().timestamp
                }
            }

            is Operation.Query.ObserveOne -> {
                return firstFailedSyncOrNull(operation.key.id.toLong())
            }

            is Operation.Query.ObserveMany -> {
                return operation.keys.firstOrNull { firstFailedSyncOrNull(it.id.toLong()) }
            }
        }
    }

    private suspend fun handleSetLastFailedSync(
        operation: Operation.Mutation<Key, Properties, Edges, Node>,
        timestamp: Long,
    ): Boolean =
        when (operation) {
            is Operation.Mutation.Create.InsertOne -> {
                trailsDatabase.postBookkeepingQueries.insertFailedCreate(operation.properties, timestamp)
                true
            }

            is Operation.Mutation.Create.InsertMany -> {
                operation.properties.forEach { properties ->
                    trailsDatabase.postBookkeepingQueries.insertFailedCreate(properties, timestamp)
                }
                true
            }

            is Operation.Mutation.Update.UpdateOne -> {
                trailsDatabase.postBookkeepingQueries.insertFailedUpdate(operation.key.id, timestamp)
                true
            }

            is Operation.Mutation.Update.UpdateMany -> {
                operation.items.forEach { item ->
                    trailsDatabase.postBookkeepingQueries.insertFailedUpdate(item.key.id, timestamp)
                }
                true
            }

            is Operation.Mutation.Upsert.UpsertOne -> {
                trailsDatabase.postBookkeepingQueries.insertFailedUpsert(operation.key.id, timestamp)
                true
            }

            is Operation.Mutation.Update.UpsertMany -> {
                operation.items.forEach { item ->
                    trailsDatabase.postBookkeepingQueries.insertFailedUpsert(item.key.id, timestamp)
                }
                true
            }

            is Operation.Mutation.Delete.DeleteOne -> {
                trailsDatabase.postBookkeepingQueries.insertFailedDelete(operation.key.id, timestamp)
                true
            }

            is Operation.Mutation.Delete.DeleteMany -> {
                operation.keys.forEach { key ->
                    trailsDatabase.postBookkeepingQueries.insertFailedDelete(key.id, timestamp)
                }
                true
            }

            is Operation.Mutation.Delete.DeleteAll -> {
                trailsDatabase.postBookkeepingQueries.insertFailedDeleteAll(timestamp)
                true
            }
        }

    private fun handleClear(operation: Operation.Mutation<Key, Properties, Edges, Node>) {
        when (operation) {
            is Operation.Query.FindOne<*> -> {
                clearFailedSyncs(operation.key.id.toLong())
            }

            is Operation.Query.FindMany<*> -> {
                operation.keys.forEach { key ->
                    clearFailedSyncs(key.id.toLong())
                }
            }

            is Operation.Query.FindAll -> {
                clearAllFailedSyncs()
            }

            is Operation.Query.ObserveOne<*> -> {
                clearFailedSyncs(operation.key.id.toLong())
            }

            is Operation.Query.ObserveMany<*> -> {
                operation.keys.forEach { key ->
                    clearFailedSyncs(key.id.toLong())
                }
            }
            is Operation.Mutation.Create.InsertOne -> {
                clearFailedSyncs(operation.key.id.toLong())
            }

            is Operation.Mutation.Create.InsertMany -> {
                operation.keys.forEach { key ->
                    clearFailedSyncs(key.id.toLong())
                }
            }

            is Operation.Mutation.Update.UpdateOne<*, *, *, *> -> {
                clearFailedSyncs(operation.key.id.toLong())
            }

            is Operation.Mutation.Update.UpdateMany<*, *, *, *> -> {
                operation.keys.forEach { key ->
                    clearFailedSyncs(key.id.toLong())
                }
            }

            is Operation.Mutation.Update.UpsertOne -> {
                clearFailedSyncs(operation.key.id.toLong())
            }

            is Operation.Mutation.Update.UpsertMany -> {
                operation.keys.forEach { key ->
                    clearFailedSyncs(key.id.toLong())
                }
            }

            is Operation.Mutation.Delete.DeleteOne -> {
                clearFailedSyncs(operation.key.id.toLong())
            }

            is Operation.Mutation.Delete.DeleteMany -> {
                operation.keys.forEach { key ->
                    clearFailedSyncs(key.id.toLong())
                }
            }

            is Operation.Mutation.Delete.DeleteAll -> {
                clearAllFailedSyncs()
            }
        }
    }

    private suspend fun firstFailedSyncOrNull(id: Long): Long? {
        val failedCreates =
            trailsDatabase.postBookkeepingQueries
                .getFailedCreates(id)
                .executeAsList()
        val failedUpdates =
            trailsDatabase.postBookkeepingQueries
                .getFailedUpdates(id)
                .executeAsList()
        val failedDeletes =
            trailsDatabase.postBookkeepingQueries
                .getFailedDeletes(id)
                .executeAsList()

        return failedCreates.firstOrNull()?.timestamp
            ?: failedUpdates.firstOrNull()?.timestamp
            ?: failedDeletes.firstOrNull()?.timestamp
    }

    private suspend fun clearFailedSyncs(id: Long) {
        trailsDatabase.postBookkeepingQueries.clearFailedCreates(id)
        trailsDatabase.postBookkeepingQueries.clearFailedUpdates(id)
        trailsDatabase.postBookkeepingQueries.clearFailedDeletes(id)
    }
}
