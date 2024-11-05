package ai.tech.customs.cms.camera.presentation

import androidx.compose.runtime.Composable
import ai.tech.core.presentation.navigation.Destination
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
public fun CameraScreen(
    navigateTo: (route: Destination) -> Unit = {},
    navigateBack: () -> Unit = {}
) {

}

@Preview
@Composable
public fun previewCameraScreen() {
    CameraScreen()
}
