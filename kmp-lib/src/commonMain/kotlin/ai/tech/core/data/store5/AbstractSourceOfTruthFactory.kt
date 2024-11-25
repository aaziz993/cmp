package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.Model
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.mobilenativefoundation.store.store5.SourceOfTruth

public abstract class AbstractSourceOfTruthFactory<
    Key : Model.Key,
    Properties : Model.Properties,
    Edges : Model.Edges,
    Node : Model.Node<Key, Properties, Edges>,
    O : Output<Model<Key, Properties, Edges>>,
    Op : Operation<Key, Properties, Edges, Node>
    >(
    private val reader: AbstractPostSourceOfTruthReader<Key, Properties, Edges, Node, O>,
    private val writer: AbstractSourceOfTruthWriter<Key, Properties, Edges, Node, O, Op>,
) {

    @Suppress("UNCHECKED_CAST")
    public fun create(): SourceOfTruth<Op, O, O> =
        SourceOfTruth.of(
            reader = { operation ->
                val mutableSharedFlow =
                    MutableSharedFlow<O?>(
                        replay = 8,
                        extraBufferCapacity = 20,
                        onBufferOverflow = BufferOverflow.DROP_OLDEST,
                    )

                reader.handleRead(operation as Operation.Query<Key, Properties, Edges, Node>) { mutableSharedFlow.emit(it) }

                mutableSharedFlow.asSharedFlow()
            },
            writer = { operation, output -> writer.handleWrite(operation, output) },
            delete = { operation -> writer.handleWrite(operation) },
            deleteAll = { writer.handleWrite(Operation.Mutation.Delete.DeleteAll) },
        )
}
