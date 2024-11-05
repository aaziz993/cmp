package ai.tech.auth.profile.presentation

import androidx.compose.runtime.Composable
import ai.tech.core.presentation.navigation.Destination
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
