package ai.tech.auth.presentation.profile

import androidx.compose.runtime.Composable
import ai.tech.navigation.presentation.Destination
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
public fun ProfileScreen(
    navigateTo: (route: Destination) -> Unit = {},
    navigateBack: () -> Unit = {}
) {

}

@Preview
@Composable
public fun previewProfileScreen() {
    ProfileScreen()
}