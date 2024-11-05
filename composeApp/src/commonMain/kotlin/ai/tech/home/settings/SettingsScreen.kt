package ai.tech.home.settings

import androidx.compose.runtime.Composable
import ai.tech.core.presentation.navigation.Destination
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
public fun SettingsScreen(
    navigateTo: (route: Destination) -> Unit = {},
    navigateBack: () -> Unit = {}
) {

}

@Preview
@Composable
public fun previewSettingsScreen() {
    SettingsScreen()
}
