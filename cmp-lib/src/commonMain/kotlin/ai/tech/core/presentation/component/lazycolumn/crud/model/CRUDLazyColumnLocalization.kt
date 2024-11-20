package ai.tech.core.presentation.component.lazycolumn.crud.model

import ai.tech.core.presentation.component.lazycolumn.model.LazyPagingColumnLocalization

public class CRUDLazyColumnLocalization(
    public val options: String = "Options",
    public val actions: String = "Actions",
    public val multiSort: String = "Multi sort",
    public val liveSearch: String = "Live search",
    public val prepend: String = "Prepend",
    public val pagination: String = "Pagination",
    public val select: String = "Select",
    public val header: String = "Header",
    public val search: String = "Search",
    noItems: String? = "No items",
    public val confirmAlert: String = "Are you sure?",
    public val confirm: String = "Confirm",
    public val cancel: String = "Cancel",
    public val valueIsNegative: String = "Value is negative",
    public val valueIsZero: String = "Value is zero",
    public val valueIsInvalid: String = "Value is invalid",
) : LazyPagingColumnLocalization(
    noItems,
)
