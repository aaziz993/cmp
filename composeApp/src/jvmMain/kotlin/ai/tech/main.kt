package ai.tech

import ai.tech.composeapp.composeapp.generated.resources.Res
import ai.tech.composeapp.composeapp.generated.resources.compose_multiplatform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension

public fun main(): Unit = application {
    // Prevent SwingPanel on top of compose components
    System.setProperty("compose.interop.blending", "true")
    System.setProperty("compose.swing.render.on.graphics", "true")

    Window(
        ::exitApplication,
        rememberWindowState(width = 1280.dp, height = 720.dp),
        title = "cmp",
        icon = painterResource(Res.drawable.compose_multiplatform)
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}