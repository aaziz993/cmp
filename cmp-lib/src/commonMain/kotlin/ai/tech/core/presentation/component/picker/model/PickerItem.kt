package ai.tech.core.presentation.component.picker.model

import ai.tech.core.presentation.component.model.item.Item
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

public open class PickerItem<T : Any>(
    text: (@Composable (Modifier) -> Unit)? = null,
    icon: (@Composable (Modifier) -> Unit)? = null,
    badge: (@Composable (Modifier) -> Unit)? = null,
    public val value: T? = null,
) : Item(text, icon, badge)
