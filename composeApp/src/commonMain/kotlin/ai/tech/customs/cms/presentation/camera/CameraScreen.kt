package ai.tech.customs.cms.presentation.camera

import androidx.compose.runtime.Composable
import ai.tech.navigation.presentation.Destination
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