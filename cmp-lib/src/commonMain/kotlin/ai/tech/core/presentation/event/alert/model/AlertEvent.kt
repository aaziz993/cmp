package ai.tech.core.presentation.event.alert.model

public data class AlertEvent(
    public val message: String,
    public val isError: Boolean = false,
    public val action: (() -> Unit)? = null
)
