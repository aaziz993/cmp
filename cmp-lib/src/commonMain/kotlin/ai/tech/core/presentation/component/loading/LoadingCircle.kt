package ai.tech.core.presentation.component.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
public fun LoadingCircle(modifier: Modifier = Modifier): Unit = Box(
    Modifier.fillMaxSize(),
    Alignment.Center
) {
    CircularProgressIndicator(modifier)
}
