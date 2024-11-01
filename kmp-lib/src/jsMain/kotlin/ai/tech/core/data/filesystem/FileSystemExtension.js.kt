package ai.tech.core.data.filesystem

import ai.tech.core.misc.platform.IS_NODE
import kotlinx.browser.window
import kotlinx.coroutines.await
import okio.FileSystem
import okio.NodeJsFileSystem

public actual fun getEnv(name: String): String? = null

public actual suspend fun String.toClipboard(): Unit =
    window.navigator.clipboard
        .writeText(this)
        .await()

public actual suspend fun fromClipboard(): String? =
    window.navigator.clipboard
        .readText()
        .await()

internal actual val fileSystem: FileSystem
    get() = if (IS_NODE) {
        NodeJsFileSystem
    }
    else {
        throw UnsupportedOperationException("FileSystem not supported in browser for security reasons. Use it only on nodejs.")
    }
