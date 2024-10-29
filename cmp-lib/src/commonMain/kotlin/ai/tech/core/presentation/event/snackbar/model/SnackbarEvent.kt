package ai.tech.core.presentation.event.snackbar.model

public data class SnackbarEvent(
    public val message: String,
    public val action: SnackbarAction? = null
)



