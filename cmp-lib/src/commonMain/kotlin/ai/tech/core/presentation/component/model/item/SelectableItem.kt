package ai.tech.core.presentation.component.model.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

public open class SelectableItem(
    text: (@Composable (Modifier) -> Unit)? = null,
    public val selectedText: (@Composable (Modifier) -> Unit)? = text,
    icon: (@Composable (Modifier) -> Unit)? = null,
    public val selectedIcon: (@Composable (Modifier) -> Unit)? = icon,
    badge: (@Composable (Modifier) -> Unit)? = null,
    public val selectedBadge: (@Composable (Modifier) -> Unit)? = badge,
    modifier: Modifier = Modifier,
    public val selectedModifier: Modifier = modifier,
) : Item(text, icon, badge, modifier) {
    @Composable
    public fun calculateText(selected: Boolean = false, modifier: Modifier = Modifier): Unit? =
        ((if (selected) selectedText else text) ?: text ?: selectedText)?.invoke(modifier)

    @Composable
    public fun calculateIcon(selected: Boolean = false, modifier: Modifier = Modifier): Unit? =
        ((if (selected) selectedIcon else icon) ?: icon ?: selectedIcon)?.invoke(modifier)

    @Composable
    public fun calculateBadge(selected: Boolean = false, modifier: Modifier = Modifier): Unit? =
        ((if (selected) selectedBadge else badge) ?: badge ?: selectedBadge)?.invoke(modifier)

    public fun calculateModifier(selected: Boolean = false): Modifier = if (selected) selectedModifier else modifier
}