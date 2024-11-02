package ai.tech.core.data.environment

import kotlinx.browser.window
import kotlinx.coroutines.await

public actual fun getEnv(name: String): String? = null

public actual suspend fun String.toClipboard(): Unit =
    window.navigator.clipboard
        .writeText(this)
        .await()

public actual suspend fun fromClipboard(): String? =
    window.navigator.clipboard
        .readText()
        .await()
