package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.DataSources
import ai.tech.core.data.store5.model.Model

public sealed class Operation<
    out K : Model.Key,
    out P : Model.Properties,
    out E : Model.Edges,
    out N : Model.Node<K, P, E>> {

    public sealed class Query<
        out K : Model.Key,
        out P : Model.Properties,
        out E : Model.Edges,
        out N : Model.Node<K, P, E>> : Operation<K, P, E, N>(){

        public abstract val dataSources: DataSources

        public data class FindOne<K : Model.Key>(val key: K, override val dataSources: DataSources) :
            Query<K, Nothing, Nothing, Nothing>()

        public data class FindMany<K : Model.Key>(val keys: List<K>, override val dataSources: DataSources) :
            Query<K, Nothing, Nothing, Nothing>()

        public data class FindAll(override val dataSources: DataSources) :
            Query<Nothing, Nothing, Nothing, Nothing>()

        public data class ObserveOne<K : Model.Key>(val key: K, override val dataSources: DataSources) :
            Query<K, Nothing, Nothing, Nothing>()

        public data class ObserveMany<K : Model.Key>(
            val keys: List<K>,
            override val dataSources: DataSources
        ) : Query<K, Nothing, Nothing, Nothing>()
    }

    public sealed class Mutation<
        out K : Model.Key,
        out P : Model.Properties,
        out E : Model.Edges,
        out N : Model.Node<K, P, E>> : Operation<K, P, E, N>() {

        public sealed class Create<K : Model.Key, P : Model.Properties, E : Model.Edges> : Mutation<K, P, E, Nothing>() {

            public data class InsertOne<P: Model.Properties>(
                val properties: P
            ) : Create<Nothing, P, Nothing>()

            public data class InsertMany<P: Model.Properties>(
                val properties: List<P>
            ) : Create<Nothing, P, Nothing>()
        }

        public sealed class Update<
            K : Model.Key,
            P : Model.Properties,
            E : Model.Edges> : Mutation<K, P, E, Nothing>() {

            public data class UpdateOne<
                K : Model.Key,
                P : Model.Properties,
                E : Model.Edges,
                N : Model.Node<K, P, E>>(
                val node: N
            ) : Update<K, P, E>()

            public data class UpdateMany<
                K : Model.Key,
                P : Model.Properties,
                E : Model.Edges,
                N : Model.Node<K, P, E>>(
                val nodes: List<N>
            ) : Update<K, P, E>()

            public data class UpsertOne<P : Model.Properties>(
                val properties: P
            ) : Update<Nothing, P, Nothing>()

            public data class UpsertMany<P : Model.Properties>(
                val properties: List<P>
            ) : Update<Nothing, P, Nothing>()
        }

        public sealed class Delete<K : Model.Key> : Mutation<K, Nothing, Nothing, Nothing>() {

            public data class DeleteOne<K : Model.Key>(
                val key: K
            ) : Delete<K>()

            public data class DeleteMany<K : Model.Key>(
                val keys: List<K>
            ) : Delete<K>()

            public data object DeleteAll : Delete<Nothing>()
        }
    }

}
