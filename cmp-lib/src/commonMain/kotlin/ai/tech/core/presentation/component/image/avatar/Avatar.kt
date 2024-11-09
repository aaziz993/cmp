package ai.tech.core.presentation.component.image.avatar

import ai.tech.core.presentation.component.image.AdvancedImage
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import ai.tech.core.presentation.component.image.avatar.model.AvatarLocalization
import kotlin.math.absoluteValue

@Composable
public fun Avatar(
    source: Any? = null,
    contentDescription: String = "",
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    onEditImage: (() -> Unit)? = null,
    onResetPassword: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null,
    localization: AvatarLocalization = AvatarLocalization(),
): Unit = if (source == null) {
    InitialsAvatar(
        firstName,
        lastName,
        modifier,
        textStyle,
    )
} else {
    AdvancedImage(
        source,
        contentDescription,
        modifier,
    )
}

@Composable
internal fun InitialsAvatar(
    firstName: String,
    lastName: String,
    modifier: Modifier,
    textStyle: TextStyle,
) {
    Box(
        modifier,
        contentAlignment = Alignment.Center,
    ) {
        val color =
            remember(firstName, lastName) {
                val name =
                    listOf(firstName, lastName)
                        .joinToString(separator = "")
                        .uppercase()
                Color(
                    (name.fold(0) { acc, char -> char.code + acc } / (name.length * 1000)).absoluteValue.toFloat(),
                    0.5f,
                    0.4f,
                )
            }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(SolidColor(color))
        }
        Text(
            text = (firstName.take(1) + lastName.take(1)).uppercase(),
            style = textStyle,
            color = Color.White,
        )
    }
}
