package ai.tech.core.presentation.component.lazycolumn.model

public open class LazyPagingColumnLocalization(
    public val notLoading: String? = "No items",
    public val refreshError: String? = "Loading failure",
    public val addError: String? = refreshError,
)
