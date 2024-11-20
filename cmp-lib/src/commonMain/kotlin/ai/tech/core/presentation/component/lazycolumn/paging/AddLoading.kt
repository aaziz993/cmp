package ai.tech.core.presentation.component.lazycolumn.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal fun LazyListScope.AddLoading() = item {
    CircularProgressIndicator(
        color = Color.Red,
        modifier = Modifier.fillMaxWidth(1f)
            .padding(20.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
    )
}
