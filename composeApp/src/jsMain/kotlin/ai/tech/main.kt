package ai.tech

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import core.di.initKoin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.GlobalContext
import org.koin.core.extension.coroutinesEngine

@OptIn(ExperimentalComposeUiApi::class)
public fun main() {
    onWasmReady {
        CanvasBasedWindow("Compose App") {
            App()
        }
    }
}