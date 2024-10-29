package ai.tech.core.presentation.component.model.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

public class NavigationItem<T : Any>(
    text: (@Composable (Modifier) -> Unit)? = null,
    selectedText: (@Composable (Modifier) -> Unit)? = text,
    icon: (@Composable (Modifier) -> Unit)? = null,
    selectedIcon: (@Composable (Modifier) -> Unit)? = icon,
    badge: (@Composable (Modifier) -> Unit)? = null,
    selectedBadge: (@Composable (Modifier) -> Unit)? = badge,
    modifier: Modifier = Modifier,
    selectedModifier: Modifier = modifier,
    public val alwaysShowLabel: Boolean = true,
    public val route: T
) : SelectableItem(
    text,
    selectedText,
    icon,
    selectedIcon,
    badge,
    selectedBadge,
    modifier,
    selectedModifier,
)