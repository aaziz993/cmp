package ai.tech.core.presentation.component.expandable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ChevronDown
import compose.icons.evaicons.outline.ChevronUp


@Composable
public fun ExpandableSection(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .clickable { isExpanded = !isExpanded }
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
    ) {
        Row(modifier = modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isExpanded) {
                    EvaIcons.Outline.ChevronUp
                } else {
                    EvaIcons.Outline.ChevronDown
                }, null
            )
            title()
        }

        AnimatedVisibility(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .fillMaxWidth(),
            visible = isExpanded
        ) {
            content()
        }
    }
}
