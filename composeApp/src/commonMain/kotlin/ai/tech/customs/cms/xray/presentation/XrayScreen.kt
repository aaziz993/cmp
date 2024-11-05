package ai.tech.customs.cms.xray.presentation

import androidx.compose.runtime.Composable
import ai.tech.core.presentation.navigation.Destination
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
public fun XrayScreen(
    navigateTo: (route: Destination) -> Unit = {},
    navigateBack: () -> Unit = {}
) {

}

@Preview
@Composable
public fun previewXrayScreen() {
    XrayScreen()
}
