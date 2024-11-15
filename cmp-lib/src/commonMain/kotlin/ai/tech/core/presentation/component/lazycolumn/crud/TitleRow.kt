package ai.tech.core.presentation.component.lazycolumn.crud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun TitleRow(
    contentPadding: PaddingValues,
    title: String,
) {
    Row(
            Modifier.fillMaxWidth().padding(contentPadding), Arrangement.SpaceAround, Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}
