package ai.tech.core.data.filesystem

import kotlinx.cinterop.toKString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.UIKit.UIPasteboard
import platform.posix.getenv

public actual fun getEnv(name: String): String? = getenv(name)?.toKString()

public actual suspend fun String.toClipboard(): Unit =
    withContext(Dispatchers.IO) {
        UIPasteboard.generalPasteboard.string = this@toClipboard
    }

public actual suspend fun fromClipboard(): String? =
    withContext(Dispatchers.IO) {
        UIPasteboard.generalPasteboard.string
    }
