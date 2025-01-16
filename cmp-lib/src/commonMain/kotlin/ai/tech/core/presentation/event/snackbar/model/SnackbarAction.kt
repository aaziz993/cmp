package ai.tech.core.presentation.event.snackbar.model

public data class SnackbarAction(
    public val name: String,
    public val action: suspend () -> Unit
)