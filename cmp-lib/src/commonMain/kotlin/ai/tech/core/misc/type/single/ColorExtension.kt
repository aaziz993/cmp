package ai.tech.core.misc.type.single

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
public fun color(error: Boolean): Color = if (error) {
    MaterialTheme.colorScheme.error
}
else {
    LocalContentColor.current
}
