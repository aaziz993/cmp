package ai.tech.core.misc.consul.client.model

/**
 * Represents the possible Check states.
 */
public enum class State {

    PASSING,
    WARNING,
    CRITICAL,
    ANY,
    UNKNOWN
}
