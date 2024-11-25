package ai.tech.core.data.store5.model

public interface Model<out K : Model.Key, out P : Model.Properties, out E : Model.Edges> {

    /** Represents a unique identifier for the model. */
    public interface Key

    /** Holds the core properties of the model. */
    public interface Properties

    /** Defines relationships to other models. */
    public interface Edges

    /** A minimal representation of the model. */
    public interface Node<out K : Key, out P : Properties, out E : Edges> : Model<K, P, E> {
        public val key: K
        public val properties: P
    }

    /** A complete representation including relationships. */
    public interface Composite<K : Key, P : Properties, E : Edges> : Model<K, P, E> {
        public val node: Node<K, P, E>
        public val edges: E
    }
}
