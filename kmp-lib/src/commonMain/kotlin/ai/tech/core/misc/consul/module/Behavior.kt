package ai.tech.core.misc.consul.module;

import kotlinx.serialization.Serializable

@Serializable
public enum class Behavior(public val label: String) {

    RELEASE("release"),

    DELETE("delete"),
}
