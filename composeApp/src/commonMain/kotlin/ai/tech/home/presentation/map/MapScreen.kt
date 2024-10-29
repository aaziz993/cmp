package ai.tech.home.presentation.map

import androidx.compose.runtime.Composable
import ai.tech.navigation.presentation.Destination
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
public fun MapScreen(
    navigateTo: (route: Destination) -> Unit = {},
    navigateBack: () -> Unit = {}
) {

}

@Preview
@Composable
public fun previewMapScreen() {
    MapScreen()
}