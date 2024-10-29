package ai.tech.auth.presentation.forgotpassword

import androidx.compose.runtime.Composable
import ai.tech.navigation.presentation.Destination
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
public fun ForgotPasswordScreen(
    navigateTo: (route: Destination) -> Unit = {},
    navigateBack: () -> Unit = {}
) {

}

@Preview
@Composable
public fun previewForgotPasswordScreen() {
    ForgotPasswordScreen()
}