package ai.tech

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

public fun main(): Unit = application {
    // Prevent SwingPanel on top of compose components
    System.setProperty("compose.interop.blending", "true")
    System.setProperty("compose.swing.render.on.graphics", "true")

    Window(
        ::exitApplication,
        rememberWindowState(width = 800.dp, height = 600.dp),
        title = "cmp"
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}